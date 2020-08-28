package domain.login.sms

import domain.login.DomainAuthenticatedUser
import io.reactivex.Single

interface SmsLogin {
  fun requestOtp(parameters: DomainRequestOneTimePasswordRequestParameters): Single<DomainSmsOneTimePassword>
  fun verifyOtp(parameters: DomainVerifyOneTimePasswordRequestParameters): Single<DomainSmsVerifiedOneTimePasswordRefreshToken>
  fun login(parameters: DomainSmsAuthRequestParameters): Single<DomainAuthenticatedUser>
}
