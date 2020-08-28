package domain.like

import domain.recommendation.DomainRecommendationUser
import io.reactivex.Single

interface LikeRecommendation {
  fun likeRecommendation(recommendation: DomainRecommendationUser)
      : Single<DomainLikedRecommendationAnswer>
}
