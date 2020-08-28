package data.tinder.login.sms

import data.ObjectMapper
import data.network.RequestFacade
import domain.login.DomainAuthenticatedUser
import domain.login.sms.DomainSmsAuthRequestParameters

internal class SmsLoginFacade(
    source: SmsLoginSource,
    requestMapper: ObjectMapper<DomainSmsAuthRequestParameters, SmsLoginRequestParameters>,
    responseMapper: ObjectMapper<SmsLoginResponse, DomainAuthenticatedUser>)
  : RequestFacade<
    DomainSmsAuthRequestParameters, SmsLoginRequestParameters, SmsLoginResponse, DomainAuthenticatedUser>(
    source, requestMapper, responseMapper)
