package domain.recommendation

data class DomainRecommendationSpotifyThemeTrack(
    val artists: Iterable<DomainRecommendationSpotifyArtist>,
    val album: DomainRecommendationSpotifyAlbum?,
    val previewUrl: String?,
    val name: String,
    val id: String,
    val uri: String)
