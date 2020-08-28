package data.tinder.recommendation

import data.database.DaoDelegate
import domain.recommendation.DomainRecommendationSpotifyThemeTrack

internal class RecommendationSpotifyThemeTrackDaoDelegate(
    private val spotifyThemeTrackDelegate: RecommendationUserSpotifyThemeTrackDao,
    private val spotifyArtistDaoDelegate: RecommendationSpotifyArtistDaoDelegate,
    private val spotifyAlbumDaoDelegate: RecommendationSpotifyAlbumDaoDelegate)
  : DaoDelegate<String?, DomainRecommendationSpotifyThemeTrack?>() {
  override fun selectByPrimaryKey(primaryKey: String?): DomainRecommendationSpotifyThemeTrack? {
    if (primaryKey == null) {
      return null
    }
    return spotifyThemeTrackDelegate.selectSpotifyThemeTrackById(primaryKey).firstOrNull()
        ?.let {
          val artists = spotifyArtistDaoDelegate.collectByPrimaryKeys(it.artists)
          it.recommendationUserSpotifyThemeTrackEntity.let {
            DomainRecommendationSpotifyThemeTrack(
                artists = artists,
                album = it.album?.let { spotifyAlbumDaoDelegate.selectByPrimaryKey(it) },
                previewUrl = it.previewUrl,
                name = it.name,
                id = it.id,
                uri = it.uri)
          }
        }
  }

  override fun insertResolved(source: DomainRecommendationSpotifyThemeTrack?) {
    source?.apply {
      album?.let { spotifyAlbumDaoDelegate.insertResolved(it) }
      spotifyArtistDaoDelegate.insertResolvedForTrackId(id, artists)
      spotifyThemeTrackDelegate.insertSpotifyThemeTrack(
          RecommendationUserSpotifyThemeTrackEntity(
              album = album?.id,
              previewUrl = previewUrl,
              name = name,
              id = id,
              uri = uri))
    }
  }
}
