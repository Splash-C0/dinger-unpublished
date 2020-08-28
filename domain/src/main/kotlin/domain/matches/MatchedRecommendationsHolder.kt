package domain.matches

object MatchedRecommendationsHolder {
  internal lateinit var matchedRecommendations: MatchedRecommendations

  fun matchedRecommendations(it: MatchedRecommendations) {
    matchedRecommendations = it
  }
}
