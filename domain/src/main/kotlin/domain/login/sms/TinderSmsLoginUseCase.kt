package domain.login.sms

import domain.interactor.CompletableDisposableUseCase
import domain.login.AccountManagementHolder
import domain.login.FailedLoginException
import io.reactivex.Completable
import io.reactivex.Scheduler

class TinderSmsLoginUseCase(
    private val phoneNumber: String,
    private val refreshToken: String,
    asyncExecutionScheduler: Scheduler? = null,
    postExecutionScheduler: Scheduler)
  : CompletableDisposableUseCase(asyncExecutionScheduler, postExecutionScheduler) {
  override fun buildUseCase(): Completable {
    return SmsLoginHolder.smsLogin
        .login(DomainSmsAuthRequestParameters(phoneNumber, refreshToken))
        .doOnSuccess {
          if (it.apiKey == null) {
            throw FailedLoginException("Retrieved Tinder API key is null")
          } else if (!AccountManagementHolder.accountManagement.setAccount(
                  accountId = phoneNumber,
                  tinderApiKey = it.apiKey,
                  refreshToken = it.refreshToken)) {
            throw FailedLoginException(
                "Failed to add account with phone number (id) $phoneNumber and apiToken ${it.apiKey}")
          }
        }
        .toCompletable()
  }
}
