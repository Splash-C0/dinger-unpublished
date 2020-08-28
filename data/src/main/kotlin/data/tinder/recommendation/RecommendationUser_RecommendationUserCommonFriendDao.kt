package data.tinder.recommendation

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
internal interface RecommendationUser_RecommendationUserCommonFriendDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertUser_CommonFriend(
      bond: RecommendationUserEntity_RecommendationUserCommonFriendEntity)
}
