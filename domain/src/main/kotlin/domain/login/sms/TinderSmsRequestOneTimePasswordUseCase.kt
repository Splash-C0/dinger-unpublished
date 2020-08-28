package domain.login.sms

import domain.interactor.SingleDisposableUseCase
import io.reactivex.Scheduler

class TinderSmsRequestOneTimePasswordUseCase(
    private val phoneNumber: String,
    asyncExecutionScheduler: Scheduler? = null,
    postExecutionScheduler: Scheduler)
  : SingleDisposableUseCase<DomainSmsOneTimePassword>(asyncExecutionScheduler, postExecutionScheduler) {
  override fun buildUseCase() =
      SmsLoginHolder.smsLogin.requestOtp(DomainRequestOneTimePasswordRequestParameters(phoneNumber))
}
