package data.tinder.login.facebook

import data.ObjectMapper
import data.network.RequestFacade
import domain.login.DomainAuthenticatedUser
import domain.login.facebook.DomainFacebookAuthRequestParameters

internal class FacebookLoginFacade(
    source: FacebookLoginSource,
    requestMapper: ObjectMapper<DomainFacebookAuthRequestParameters, FacebookLoginRequestParameters>,
    responseMapper: ObjectMapper<FacebookLoginResponse, DomainAuthenticatedUser>)
  : RequestFacade<
    DomainFacebookAuthRequestParameters, FacebookLoginRequestParameters, FacebookLoginResponse, DomainAuthenticatedUser>(
    source, requestMapper, responseMapper)
