package data.tinder.recommendation

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
internal interface RecommendationUserJobDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertJob(job: RecommendationUserJobEntity)

  @Query("SELECT * from RecommendationUserJobEntity WHERE id=:id")
  fun selectJobById(id: String): List<RecommendationUserJobEntity>
}
