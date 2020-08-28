package domain.login.sms

import domain.interactor.CompletableDisposableUseCase
import domain.login.AccountManagementHolder
import domain.login.FailedLoginException
import io.reactivex.Scheduler
import java.util.UUID

class TinderSmsVerifyOneTimePasswordUseCase(
    private val phoneNumber: String,
    private val otp: String,
    asyncExecutionScheduler: Scheduler? = null,
    postExecutionScheduler: Scheduler)
  : CompletableDisposableUseCase(asyncExecutionScheduler, postExecutionScheduler) {
  override fun buildUseCase() =
      SmsLoginHolder.smsLogin.verifyOtp(
          DomainVerifyOneTimePasswordRequestParameters(phoneNumber, otp))
          .doOnSuccess {
            if (!AccountManagementHolder.accountManagement.setAccount(
                    accountId = phoneNumber,
                    // Generate one that we know will fail so we receive 401s instead of 403s in requests that need authentication. This will trigger authentication and have everything work fine
                    tinderApiKey = UUID.randomUUID().toString(),
                    refreshToken = it.refreshToken)) {
              throw FailedLoginException(
                  "Failed to add account with phone number (id) $phoneNumber and refreshToken ${it.refreshToken}")
            }
          }.toCompletable()!!
}
