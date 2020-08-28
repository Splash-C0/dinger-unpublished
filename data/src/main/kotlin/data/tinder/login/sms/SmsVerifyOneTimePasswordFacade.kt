package data.tinder.login.sms

import data.ObjectMapper
import data.network.RequestFacade
import domain.login.sms.DomainSmsVerifiedOneTimePasswordRefreshToken
import domain.login.sms.DomainVerifyOneTimePasswordRequestParameters

internal class SmsVerifyOneTimePasswordFacade(
    source: SmsVerifyOneTimePasswordSource,
    requestMapper: ObjectMapper<DomainVerifyOneTimePasswordRequestParameters, SmsVerifyOneTimePasswordRequestParameters>,
    responseMapper: ObjectMapper<SmsVerifyOneTimePasswordResponse, DomainSmsVerifiedOneTimePasswordRefreshToken>)
  : RequestFacade<
    DomainVerifyOneTimePasswordRequestParameters, SmsVerifyOneTimePasswordRequestParameters, SmsVerifyOneTimePasswordResponse, DomainSmsVerifiedOneTimePasswordRefreshToken>(
    source, requestMapper, responseMapper)
