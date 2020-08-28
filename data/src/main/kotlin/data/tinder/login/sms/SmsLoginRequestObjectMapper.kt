package data.tinder.login.sms

import data.ObjectMapper
import domain.login.sms.DomainSmsAuthRequestParameters
import reporter.CrashReporter

internal class SmsLoginRequestObjectMapper(crashReporter: CrashReporter)
  : ObjectMapper<DomainSmsAuthRequestParameters, SmsLoginRequestParameters>(crashReporter) {
  override fun fromImpl(source: DomainSmsAuthRequestParameters) = source.run {
    SmsLoginRequestParameters(phoneNumber, refreshToken)
  }
}
