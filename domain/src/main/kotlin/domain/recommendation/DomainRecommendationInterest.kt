package domain.recommendation

data class DomainRecommendationLike(val id: String, val name: String) {
  companion object {
    val NONE = DomainRecommendationLike(id = "", name = "")
  }
}
