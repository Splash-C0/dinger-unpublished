package data.tinder.recommendation

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
internal interface RecommendationLikeDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertLike(like: RecommendationLikeEntity)

  @Query("SELECT * from RecommendationLikeEntity WHERE id=:id")
  fun selectLikeById(id: String): List<RecommendationLikeEntity>
}
