package domain.recommendation

data class DomainRecommendationPhoto(
    val id: String,
    val url: String,
    val processedFiles: Iterable<DomainRecommendationProcessedFile>) {
  companion object {
    val NONE = DomainRecommendationPhoto("", "", emptySet())
  }
}
