package data.tinder.recommendation

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
internal interface RecommendationUserSpotifyThemeTrackAlbumDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertAlbum(album: RecommendationUserSpotifyThemeTrackAlbumEntity)

  @Query("SELECT * from RecommendationUserSpotifyThemeTrackAlbumEntity WHERE id=:id")
  @Transaction
  fun selectAlbumById(id: String)
      : List<RecommendationUserSpotifyThemeTrackAlbumWithRelatives>
}
