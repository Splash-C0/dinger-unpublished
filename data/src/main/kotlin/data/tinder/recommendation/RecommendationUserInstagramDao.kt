package data.tinder.recommendation

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
internal interface RecommendationUserInstagramDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertInstagram(instagram: RecommendationUserInstagramEntity)

  @Query("SELECT * from RecommendationUserInstagramEntity WHERE username=:username")
  @Transaction
  fun selectInstagramByUsername(username: String): List<RecommendationUserInstagramWithRelatives>
}
