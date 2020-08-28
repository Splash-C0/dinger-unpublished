package data.tinder.recommendation

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
internal interface RecommendationUser_PhotoDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertUser_Photo(bond: RecommendationUserEntity_RecommendationUserPhotoEntity)
}
