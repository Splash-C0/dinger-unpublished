package data.seen

import data.tinder.recommendation.RecommendationUserResolver
import domain.seen.SeenRecommendations

internal class SeenRecommendationsImpl(private val resolver: RecommendationUserResolver)
  : SeenRecommendations {
  override fun filter(filter: String) = resolver.selectByFilter(filter)
}
