package data.tinder.recommendation

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
internal interface RecommendationUserPhotoDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertPhoto(photo: RecommendationUserPhotoEntity)

  @Query("SELECT * from RecommendationUserPhotoEntity WHERE id=:id")
  @Transaction
  fun selectPhotoById(id: String): List<RecommendationUserPhotoWithRelatives>
}
