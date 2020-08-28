package domain.profile

import io.reactivex.Single

interface GetProfile {
  fun getProfile(): Single<DomainGetProfileAnswer>
}
