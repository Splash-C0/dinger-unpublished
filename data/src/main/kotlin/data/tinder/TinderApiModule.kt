package data.tinder

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import dagger.Module
import dagger.Provides
import data.RootModule
import data.account.AccountModule
import data.account.AppAccountAuthenticator
import data.crash.CrashReporterModule
import data.network.FlavoredNetworkClientModule
import data.network.NetworkModule
import data.notification.NotificationManager
import data.notification.NotificationManagerModule
import data.tinder.login.facebook.FacebookAuthenticator
import data.tinder.login.sms.SmsAuthenticator
import domain.logout.LogoutUseCase
import io.reactivex.observers.DisposableCompletableObserver
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.Request
import org.stoyicker.dinger.data.BuildConfig
import org.stoyicker.dinger.data.R
import reporter.CrashReporter
import retrofit2.Retrofit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module(includes = [
  AccountModule::class,
  CrashReporterModule::class,
  FlavoredNetworkClientModule::class,
  NetworkModule::class,
  NotificationManagerModule::class,
  RootModule::class])
internal class TinderApiModule {
  @Provides
  @Singleton
  @Local
  fun facebookAuthenticator(
      appAccountAuthenticator: AppAccountAuthenticator,
      crashReporter: CrashReporter) = FacebookAuthenticator(appAccountAuthenticator, crashReporter)

  @Provides
  @Singleton
  @Local
  fun smsAuthenticator(
      appAccountAuthenticator: AppAccountAuthenticator,
      crashReporter: CrashReporter) = SmsAuthenticator(appAccountAuthenticator, crashReporter)

  @Provides
  @Singleton
  fun tinderApi(
      context: Context,
      clientBuilder: OkHttpClient.Builder,
      retrofitBuilder: Retrofit.Builder,
      appAccountAuthenticator: AppAccountAuthenticator,
      notificationManager: NotificationManager,
      @Local facebookAuthenticator: FacebookAuthenticator,
      @Local smsAuthenticator: SmsAuthenticator,
      crashReporter: CrashReporter): TinderApi = retrofitBuilder
      .client(clientBuilder
          .addInterceptor {
            it.proceed(it.request().newBuilder().addHeaders(appAccountAuthenticator).build())
          }
          // Won't work without the explicit anonymous class declaration
          .authenticator { route, response ->
            if (route?.address()?.url()?.toString()?.contains("auth/login") != false) {
              return@authenticator null
            }
            if (isRefreshing) {
              return@authenticator null
            }
            if (!appAccountAuthenticator.isThereALoggedInUser()) {
              return@authenticator null
            }
            isRefreshing = true
            var result = facebookAuthenticator.authenticate(route, response)
            if (result == null) {
              isRefreshing = true
              result = smsAuthenticator.authenticate(route, response)
            }
            if (result == null) {
              finishAccount(context, crashReporter, notificationManager)
            }
            result
          }.dispatcher(Dispatcher().apply {
            maxRequestsPerHost = 30
          })
          .build())
      .baseUrl(TinderApi.BASE_URL)
      .build()
      .create(TinderApi::class.java)

  @Retention(AnnotationRetention.RUNTIME)
  @Qualifier
  private annotation class Local
}

// FIXME Could cause issues if there are concurrent requests
var isRefreshing = false

internal fun Request.Builder.addHeaders(appAccountAuthenticator: AppAccountAuthenticator) = apply {
  if (!isRefreshing) {
    appAccountAuthenticator.getApiToken()?.let {
      header(TinderApi.HEADER_AUTH, it)
    }
  }
  header(TinderApi.HEADER_CONTENT_TYPE, TinderApi.CONTENT_TYPE_JSON)
  header(TinderApi.HEADER_PLATFORM, BuildConfig.PLATFORM_ANDROID)
}

private fun finishAccount(
    context: Context,
    crashReporter: CrashReporter,
    notificationManager: NotificationManager) {
  with(notificationManager) {
    cancelAll()
    show(build(
        channelName = R.string.authentication_notification_channel_name,
        title = R.string.authentication_notification_title,
        body = R.string.authentication_notification_body,
        category = NotificationManager.CATEGORY_RECOMMENDATION,
        priority = NotificationManager.PRIORITY_HIGH,
        clickHandler = PendingIntent.getActivity(
            context,
            0,
            Intent().setComponent(ComponentName(context, "app.entryscreen.splash.SplashActivity")),
            PendingIntent.FLAG_UPDATE_CURRENT),
        notificationId = NotificationManager.ID_ACCOUNT))
  }
  LogoutUseCase(context).execute(object : DisposableCompletableObserver() {
    override fun onError(e: Throwable) = crashReporter.report(e)

    override fun onComplete() = Unit
  })
}
