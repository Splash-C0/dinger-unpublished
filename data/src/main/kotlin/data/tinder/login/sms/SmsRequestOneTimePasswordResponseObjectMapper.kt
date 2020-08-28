package data.tinder.login.sms

import data.ObjectMapper
import domain.login.sms.DomainSmsOneTimePassword
import reporter.CrashReporter

internal class SmsRequestOneTimePasswordResponseObjectMapper(crashReporter: CrashReporter)
  : ObjectMapper<SmsRequestOneTimePasswordResponse, DomainSmsOneTimePassword>(crashReporter) {
  override fun fromImpl(source: SmsRequestOneTimePasswordResponse) = source.data.let {
    DomainSmsOneTimePassword(it.otpLength, it.smsSent)
  }
}
