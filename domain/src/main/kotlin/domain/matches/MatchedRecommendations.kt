package domain.matches

import androidx.paging.DataSource
import domain.recommendation.DomainRecommendationUser

interface MatchedRecommendations {
  fun filter(filter: String): DataSource.Factory<Int, DomainRecommendationUser>
}
