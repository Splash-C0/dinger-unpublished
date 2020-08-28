package data.tinder.login.sms

import data.ObjectMapper
import domain.login.sms.DomainSmsVerifiedOneTimePasswordRefreshToken
import reporter.CrashReporter

internal class SmsVerifyOneTimePasswordResponseObjectMapper(crashReporter: CrashReporter)
  : ObjectMapper<SmsVerifyOneTimePasswordResponse, DomainSmsVerifiedOneTimePasswordRefreshToken>(crashReporter) {
  override fun fromImpl(source: SmsVerifyOneTimePasswordResponse) = source.data.run {
    DomainSmsVerifiedOneTimePasswordRefreshToken(refreshToken)
  }
}
