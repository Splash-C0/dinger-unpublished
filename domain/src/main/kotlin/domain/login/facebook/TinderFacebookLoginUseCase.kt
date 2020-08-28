package domain.login.facebook

import domain.interactor.CompletableDisposableUseCase
import domain.login.AccountManagementHolder
import domain.login.FailedLoginException
import io.reactivex.Completable
import io.reactivex.Scheduler

class TinderFacebookLoginUseCase(
    private val facebookId: String,
    private val facebookToken: String,
    asyncExecutionScheduler: Scheduler? = null,
    postExecutionScheduler: Scheduler)
  : CompletableDisposableUseCase(asyncExecutionScheduler, postExecutionScheduler) {
  override fun buildUseCase(): Completable {
    return FacebookLoginHolder.facebookLogin
        .login(DomainFacebookAuthRequestParameters(facebookId, facebookToken))
        .doOnSuccess {
          if (it.apiKey == null) {
            throw FailedLoginException("Retrieved Tinder API key is null")
          } else if (!AccountManagementHolder.accountManagement.setAccount(
                  accountId = facebookId,
                  tinderApiKey = it.apiKey)) {
            throw FailedLoginException(
                "Failed to add account with fbId $facebookId and apiToken ${it.apiKey}")
          }
        }
        .toCompletable()
  }
}
