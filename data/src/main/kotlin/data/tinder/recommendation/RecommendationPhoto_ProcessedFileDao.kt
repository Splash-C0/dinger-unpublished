package data.tinder.recommendation

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
internal interface RecommendationPhoto_ProcessedFileDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertPhoto_ProcessedFile(
      bond: RecommendationUserPhotoEntity_RecommendationUserPhotoProcessedFileEntity)
}
