package data.tinder.login.facebook

import domain.login.DomainAuthenticatedUser
import domain.login.facebook.DomainFacebookAuthRequestParameters
import domain.login.facebook.FacebookLogin
import io.reactivex.Single
import reporter.CrashReporter

internal class FacebookLoginImpl(
    private val loginFacade: FacebookLoginFacade,
    private val crashReporter: CrashReporter) : FacebookLogin {
  override fun login(parameters: DomainFacebookAuthRequestParameters): Single<DomainAuthenticatedUser> =
      loginFacade.fetch(parameters).doOnError { crashReporter.report(it) }
}
