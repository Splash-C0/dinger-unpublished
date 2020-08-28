package data.tinder.login.sms

import data.ObjectMapper
import data.network.RequestFacade
import domain.login.sms.DomainRequestOneTimePasswordRequestParameters
import domain.login.sms.DomainSmsOneTimePassword

internal class SmsRequestOneTimePasswordFacade(
    source: SmsRequestOneTimePasswordSource,
    requestMapper: ObjectMapper<DomainRequestOneTimePasswordRequestParameters, SmsRequestOneTimePasswordRequestParameters>,
    responseMapper: ObjectMapper<SmsRequestOneTimePasswordResponse, DomainSmsOneTimePassword>)
  : RequestFacade<
    DomainRequestOneTimePasswordRequestParameters, SmsRequestOneTimePasswordRequestParameters, SmsRequestOneTimePasswordResponse, DomainSmsOneTimePassword>(
    source, requestMapper, responseMapper)
