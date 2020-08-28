package data.tinder.recommendation

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
internal interface RecommendationSpotifyAlbum_ProcessedFileDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertSpotifyAlbum_ProcessedFile(
      bond: RecommendationUserSpotifyThemeTrackAlbumEntity_RecommendationUserPhotoProcessedFileEntity)
}
