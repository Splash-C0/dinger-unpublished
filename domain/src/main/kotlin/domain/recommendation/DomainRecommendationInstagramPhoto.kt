package domain.recommendation

data class DomainRecommendationInstagramPhoto constructor(
    val link: String?,
    val imageUrl: String,
    val thumbnailUrl: String,
    val ts: String) {
  companion object {
    val NONE = DomainRecommendationInstagramPhoto(
        link = null,
        imageUrl = "",
        thumbnailUrl = "",
        ts = "")
  }
}
