package data.tinder.recommendation

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
internal interface RecommendationUserInstagramPhotoDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertInstagramPhoto(photo: RecommendationUserInstagramPhotoEntity)

  @Query("SELECT * from RecommendationUserInstagramPhotoEntity WHERE link=:link")
  fun selectInstagramPhotoByLink(link: String): List<RecommendationUserInstagramPhotoEntity>
}
