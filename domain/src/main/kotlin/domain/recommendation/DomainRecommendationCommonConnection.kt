package domain.recommendation

data class DomainRecommendationCommonFriend(
    val id: String,
    val name: String,
    val degree: String,
    val photos: Iterable<DomainRecommendationCommonFriendPhoto>?) {
  companion object {
    val NONE = DomainRecommendationCommonFriend(
        id = "",
        name = "",
        degree = "",
        photos = emptySet())
  }
}
