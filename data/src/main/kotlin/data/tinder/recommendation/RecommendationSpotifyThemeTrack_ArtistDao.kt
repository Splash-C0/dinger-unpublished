package data.tinder.recommendation

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
internal interface RecommendationSpotifyThemeTrack_ArtistDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertSpotifyThemeTrack_Artist(
      bond: RecommendationUserSpotifyThemeTrackEntity_RecommendationUserSpotifyThemeTrackArtistEntity)
}
