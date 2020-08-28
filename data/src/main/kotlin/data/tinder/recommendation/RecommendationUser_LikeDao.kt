package data.tinder.recommendation

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
internal interface RecommendationUser_LikeDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertUser_Like(bond: RecommendationUserEntity_RecommendationLikeEntity)
}
