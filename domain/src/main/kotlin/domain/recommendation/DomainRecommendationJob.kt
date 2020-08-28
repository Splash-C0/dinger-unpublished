package domain.recommendation

data class DomainRecommendationJob(
    val id: String,
    val company: DomainRecommendationJobCompany?,
    val title: DomainRecommendationJobTitle?) {
  companion object {
    val NONE = DomainRecommendationJob(
        id = "",
        company = null,
        title = null)
  }
}
