package data.tinder.matches

import data.tinder.recommendation.RecommendationUserResolver
import domain.matches.MatchedRecommendations

internal class MatchedRecommendationsImpl(private val resolver: RecommendationUserResolver)
  : MatchedRecommendations {
  override fun filter(filter: String) = resolver.selectMatchedByFilterOnName(filter)
}
