package data.tinder.login.facebook

import com.facebook.AccessToken
import com.facebook.FacebookException
import data.account.AppAccountAuthenticator
import data.tinder.addHeaders
import data.tinder.isRefreshing
import domain.login.facebook.TinderFacebookLoginUseCase
import io.reactivex.Single
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import reporter.CrashReporter

internal class FacebookAuthenticator(
    private val appAccountManager: AppAccountAuthenticator,
    private val crashReporter: CrashReporter) : Authenticator {
  override fun authenticate(route: Route?, response: Response) = when {
    AccessToken.getCurrentAccessToken() == null -> {
      // This session is not using a Facebook account-generated token
      null
    }
    else -> appAccountManager.let {
      Single.create<AccessToken> { emitter ->
        if (AccessToken.getCurrentAccessToken()!!.isExpired) {
          AccessToken.refreshCurrentAccessTokenAsync(
              object : AccessToken.AccessTokenRefreshCallback {
                override fun OnTokenRefreshed(
                    accessToken: AccessToken) {
                  emitter.onSuccess(accessToken)
                }

                override fun OnTokenRefreshFailed(
                    exception: FacebookException) {
                  emitter.onError(exception)
                }
              })
        } else {
          emitter.onSuccess(AccessToken.getCurrentAccessToken()!!)
        }
      }.run {
        try {
          blockingGet().run {
            AccessToken.setCurrentAccessToken(this)
            var request: Request? = null
            TinderFacebookLoginUseCase(
                facebookId = userId,
                facebookToken = token,
                postExecutionScheduler = Schedulers.trampoline())
                .execute(object : DisposableCompletableObserver() {
                  override fun onComplete() {
                    isRefreshing = false
                    request = response.request().newBuilder()
                        .addHeaders(appAccountManager)
                        .build()
                  }

                  override fun onError(error: Throwable) {
                    isRefreshing = false
                    crashReporter.report(error)
                  }
                })
            request
          }
        } catch (exception: Throwable) {
          isRefreshing = false
          crashReporter.report(exception)
          null
        }
      }
    }
  }
}
