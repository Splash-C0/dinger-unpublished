package domain.recommendation

import io.reactivex.Single

interface GetRecommendation {
  fun getRecommendations(): Single<List<DomainRecommendationUser>>
}
