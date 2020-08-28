package data.tinder.login.sms

import data.ObjectMapper
import domain.login.DomainAuthenticatedUser
import domain.login.sms.DomainSmsVerifiedOneTimePasswordRefreshToken
import reporter.CrashReporter

internal class SmsLoginResponseObjectMapper(crashReporter: CrashReporter)
  : ObjectMapper<SmsLoginResponse, DomainAuthenticatedUser>(crashReporter) {
  override fun fromImpl(source: SmsLoginResponse) = source.data.run {
    DomainAuthenticatedUser(apiKey = apiToken, isNewUser = isNewUser, refreshToken = refreshToken)
  }
}
