package data.tinder.login.sms

import data.account.AppAccountAuthenticator
import data.tinder.addHeaders
import data.tinder.isRefreshing
import domain.login.sms.TinderSmsLoginUseCase
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import reporter.CrashReporter

internal class SmsAuthenticator(
    private val appAccountManager: AppAccountAuthenticator,
    private val crashReporter: CrashReporter) : Authenticator {
  override fun authenticate(route: Route?, response: Response): Request? {
    val accId = appAccountManager.getAccountId()
    val refreshToken = appAccountManager.getRefreshToken()
    if (accId == null || refreshToken == null) {
      return null
    }
    var request: Request? = null
    TinderSmsLoginUseCase(
        phoneNumber = accId,
        refreshToken = refreshToken,
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
    return request
  }
}
