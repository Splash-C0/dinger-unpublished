package data.tinder.recommendation

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
internal interface RecommendationProcessedFileDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertProcessedFile(file: RecommendationUserPhotoProcessedFileEntity)

  @Query("SELECT * from RecommendationUserPhotoProcessedFileEntity WHERE url=:url")
  fun selectProcessedFileByUrl(url: String): List<RecommendationUserPhotoProcessedFileEntity>
}
