package domain.dislike

import domain.recommendation.DomainRecommendationUser
import io.reactivex.Single

interface DislikeRecommendation {
  fun dislikeRecommendation(recommendation: DomainRecommendationUser)
      : Single<DomainDislikedRecommendationAnswer>
}
