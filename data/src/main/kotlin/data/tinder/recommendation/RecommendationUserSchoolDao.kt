package data.tinder.recommendation

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
internal interface RecommendationUserSchoolDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertSchool(school: RecommendationUserSchoolEntity)

  @Query("SELECT * from RecommendationUserSchoolEntity WHERE id=:id")
  fun selectSchoolById(id: String): List<RecommendationUserSchoolEntity>
}
