package data.tinder.login.sms

import data.ObjectMapper
import domain.login.sms.DomainVerifyOneTimePasswordRequestParameters
import reporter.CrashReporter

internal class SmsVerifyOneTimePasswordRequestObjectMapper(crashReporter: CrashReporter)
  : ObjectMapper<DomainVerifyOneTimePasswordRequestParameters, SmsVerifyOneTimePasswordRequestParameters>(crashReporter) {
  override fun fromImpl(source: DomainVerifyOneTimePasswordRequestParameters) = source.run {
    SmsVerifyOneTimePasswordRequestParameters(
        phoneNumber = phoneNumber,
        otp = otp)
  }
}
