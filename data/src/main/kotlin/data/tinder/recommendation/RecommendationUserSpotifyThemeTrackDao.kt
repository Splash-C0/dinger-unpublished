package data.tinder.recommendation

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
internal interface RecommendationUserSpotifyThemeTrackDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertSpotifyThemeTrack(track: RecommendationUserSpotifyThemeTrackEntity)

  @Query("SELECT * from RecommendationUserSpotifyThemeTrackEntity WHERE id=:id")
  @Transaction
  fun selectSpotifyThemeTrackById(id: String)
      : List<RecommendationUserSpotifyThemeTrackWithRelatives>
}
