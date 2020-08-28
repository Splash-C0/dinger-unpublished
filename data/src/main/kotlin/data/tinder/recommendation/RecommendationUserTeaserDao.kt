package data.tinder.recommendation

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
internal interface RecommendationUserTeaserDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertTeaser(teaser: RecommendationUserTeaserEntity)

  @Query("SELECT * from RecommendationUserTeaserEntity WHERE id=:id")
  fun selectTeaserById(id: String?): List<RecommendationUserTeaserEntity>
}
