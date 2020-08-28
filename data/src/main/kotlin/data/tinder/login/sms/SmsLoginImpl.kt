package data.tinder.login.sms

import domain.login.DomainAuthenticatedUser
import domain.login.sms.DomainRequestOneTimePasswordRequestParameters
import domain.login.sms.DomainSmsAuthRequestParameters
import domain.login.sms.DomainVerifyOneTimePasswordRequestParameters
import domain.login.sms.SmsLogin
import io.reactivex.Single
import reporter.CrashReporter

internal class SmsLoginImpl(
    private val requestOneTimePasswordFacade: SmsRequestOneTimePasswordFacade,
    private val verifyOneTimePasswordFacade: SmsVerifyOneTimePasswordFacade,
    private val smsLoginFacade: SmsLoginFacade,
    private val crashReporter: CrashReporter) : SmsLogin {
  override fun requestOtp(parameters: DomainRequestOneTimePasswordRequestParameters) =
      requestOneTimePasswordFacade.fetch(parameters).doOnError { crashReporter.report(it) }

  override fun verifyOtp(parameters: DomainVerifyOneTimePasswordRequestParameters) =
      verifyOneTimePasswordFacade.fetch(parameters).doOnError { crashReporter.report(it) }

  override fun login(parameters: DomainSmsAuthRequestParameters): Single<DomainAuthenticatedUser> =
      smsLoginFacade.fetch(parameters).doOnError { crashReporter.report(it) }
}
