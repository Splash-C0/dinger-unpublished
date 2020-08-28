package data.tinder.login.sms

import data.ObjectMapper
import domain.login.sms.DomainRequestOneTimePasswordRequestParameters
import reporter.CrashReporter

internal class SmsRequestOneTimePasswordRequestObjectMapper(crashReporter: CrashReporter)
  : ObjectMapper<DomainRequestOneTimePasswordRequestParameters, SmsRequestOneTimePasswordRequestParameters>(crashReporter) {
  override fun fromImpl(source: DomainRequestOneTimePasswordRequestParameters) =
      SmsRequestOneTimePasswordRequestParameters(source.phoneNumber)
}
