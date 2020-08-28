package domain.login.facebook

import domain.login.DomainAuthenticatedUser
import io.reactivex.Single

interface FacebookLogin {
  fun login(parameters: DomainFacebookAuthRequestParameters): Single<DomainAuthenticatedUser>
}
