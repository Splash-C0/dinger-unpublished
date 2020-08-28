package data.tinder.login.facebook

import data.ObjectMapper
import domain.login.facebook.DomainFacebookAuthRequestParameters
import reporter.CrashReporter

internal class FacebookLoginRequestObjectMapper(crashReporter: CrashReporter)
  : ObjectMapper<DomainFacebookAuthRequestParameters, FacebookLoginRequestParameters>(crashReporter) {
  override fun fromImpl(source: DomainFacebookAuthRequestParameters) =
      source.let {
        FacebookLoginRequestParameters(it.facebookId, it.facebookToken)
      }
}
