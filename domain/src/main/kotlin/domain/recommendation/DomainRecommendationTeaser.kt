package domain.recommendation

data class DomainRecommendationTeaser constructor(
    val id: String,
    val description: String,
    val type: String?) {
  companion object {
    val NONE = DomainRecommendationTeaser(id = "", description = "", type = null)
  }
}
