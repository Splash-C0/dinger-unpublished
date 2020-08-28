package domain.seen

import androidx.paging.DataSource
import domain.recommendation.DomainRecommendationUser

interface SeenRecommendations {
  fun filter(filter: String): DataSource.Factory<Int, DomainRecommendationUser>
}
