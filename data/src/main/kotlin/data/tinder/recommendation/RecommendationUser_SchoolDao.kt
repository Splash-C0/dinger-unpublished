package data.tinder.recommendation

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
internal interface RecommendationUser_SchoolDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertUser_School(bond: RecommendationUserEntity_RecommendationUserSchoolEntity)
}
