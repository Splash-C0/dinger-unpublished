package data.tinder.recommendation

import data.database.CollectibleDaoDelegate
import domain.recommendation.DomainRecommendationSpotifyArtist

internal class RecommendationSpotifyArtistDaoDelegate(
    private val artistDao: RecommendationSpotifyArtistDao,
    private val trackArtistDao: RecommendationSpotifyThemeTrack_ArtistDao)
  : CollectibleDaoDelegate<String, DomainRecommendationSpotifyArtist>() {
  override fun selectByPrimaryKey(primaryKey: String) =
      artistDao.selectArtistById(primaryKey).firstOrNull()?.let {
        return DomainRecommendationSpotifyArtist(
            id = it.id,
            name = it.name)
      } ?: DomainRecommendationSpotifyArtist.NONE

  override fun insertResolved(source: DomainRecommendationSpotifyArtist) =
      artistDao.insertArtist(RecommendationUserSpotifyThemeTrackArtistEntity(
          id = source.id,
          name = source.name))

  fun insertResolvedForTrackId(
      trackId: String, artists: Iterable<DomainRecommendationSpotifyArtist>) =
      artists.forEach {
        insertResolved(it)
        trackArtistDao.insertSpotifyThemeTrack_Artist(
            RecommendationUserSpotifyThemeTrackEntity_RecommendationUserSpotifyThemeTrackArtistEntity(
                recommendationUserSpotifyThemeTrackArtistEntityId = trackId,
                recommendationUserSpotifyThemeTrackEntityId = it.id))
      }
}
