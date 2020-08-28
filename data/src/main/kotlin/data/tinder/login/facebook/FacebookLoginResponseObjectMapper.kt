package data.tinder.login.facebook

import data.ObjectMapper
import domain.login.DomainAuthenticatedUser
import reporter.CrashReporter

internal class FacebookLoginResponseObjectMapper(crashReporter: CrashReporter)
  : ObjectMapper<FacebookLoginResponse, DomainAuthenticatedUser>(crashReporter) {
  override fun fromImpl(source: FacebookLoginResponse) =
      DomainAuthenticatedUser(source.data.apiToken, source.data.isNewUser, null)
}
