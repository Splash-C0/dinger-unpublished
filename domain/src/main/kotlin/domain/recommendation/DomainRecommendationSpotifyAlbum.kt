package domain.recommendation

data class DomainRecommendationSpotifyAlbum(
    val id: String,
    val name: String,
    val images: Iterable<DomainRecommendationProcessedFile>) {
  companion object {
    val NONE = DomainRecommendationSpotifyAlbum(id = "", name = "", images = emptySet())
  }
}
