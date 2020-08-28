package data.tinder.recommendation

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
internal interface RecommendationSpotifyArtistDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertArtist(artist: RecommendationUserSpotifyThemeTrackArtistEntity)

  @Query("SELECT * from RecommendationUserSpotifyThemeTrackArtistEntity WHERE id=:id")
  fun selectArtistById(id: String): List<RecommendationUserSpotifyThemeTrackArtistEntity>
}
