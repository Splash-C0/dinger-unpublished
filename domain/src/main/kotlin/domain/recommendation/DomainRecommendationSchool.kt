package domain.recommendation

data class DomainRecommendationSchool(val id: String?, val name: String) {
  companion object {
    val NONE = DomainRecommendationSchool(id = null, name = "")
  }
}
