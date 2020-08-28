package data.tinder.recommendation

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
internal interface RecommendationUserInstagram_InstagramPhotoDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertInstagram_Photo(
      bond: RecommendationUserInstagramEntity_RecommendationUserInstagramPhotoEntity)
}
