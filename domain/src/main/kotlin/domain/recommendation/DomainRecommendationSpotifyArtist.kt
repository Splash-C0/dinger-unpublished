package domain.recommendation

data class DomainRecommendationSpotifyArtist(val id: String, val name: String) {
  companion object {
    val NONE = DomainRecommendationSpotifyArtist(id = "", name = "")
  }
}
