package domain.seen

import androidx.lifecycle.LiveData
import domain.recommendation.DomainRecommendationUser

interface SeenRecommendation {
  fun get(id: String): LiveData<DomainRecommendationUser>
}
