package data.tinder.recommendation

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
internal interface RecommendationUserCommonFriendDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertCommonFriend(commonFriend: RecommendationUserCommonFriendEntity)

  @Query("SELECT * from RecommendationUserCommonFriendEntity WHERE id=:id")
  @Transaction
  fun selectCommonFriendById(id: String)
      : List<RecommendationUserCommonFriendWithRelatives>
}
