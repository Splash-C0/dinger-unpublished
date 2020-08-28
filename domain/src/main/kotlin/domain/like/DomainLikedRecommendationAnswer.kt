package domain.like

data class DomainLikedRecommendationAnswer(
    val matched: Boolean, val rateLimitedUntilMillis: Long?) {
  companion object {
    val EMPTY = DomainLikedRecommendationAnswer(matched = false, rateLimitedUntilMillis = null)
  }
}
