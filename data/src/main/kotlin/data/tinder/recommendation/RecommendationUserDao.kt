package data.tinder.recommendation

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
internal interface RecommendationUserDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertUser(user: RecommendationUserEntity)

  @Query("SELECT * from RecommendationUserEntity WHERE id=:id")
  @Transaction
  fun selectUserById(id: String): LiveData<List<RecommendationUserWithRelatives>>

  @Query("SELECT * from RecommendationUserEntity WHERE instr(filterableContent, :filter) > 0 ORDER by insertionEpoch DESC")
  @Transaction
  fun selectUsersByFilter(filter: String): DataSource.Factory<Int, RecommendationUserWithRelatives>

  @Query("SELECT * from RecommendationUserEntity WHERE instr(filterableContent, :filter) > 0 AND matched = 1 ORDER by insertionEpoch DESC")
  @Transaction
  fun selectMatchedUsersByFilterOnName(filter: String)
      : DataSource.Factory<Int, RecommendationUserWithRelatives>
}
