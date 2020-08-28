package data.seen

import data.tinder.recommendation.RecommendationUserResolver
import domain.seen.SeenRecommendation

internal class SeenRecommendationImpl(private val resolver: RecommendationUserResolver)
  : SeenRecommendation {
  override fun get(id: String) = resolver.selectById(id)
}
