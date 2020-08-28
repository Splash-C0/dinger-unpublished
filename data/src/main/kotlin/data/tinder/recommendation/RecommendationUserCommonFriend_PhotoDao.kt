package data.tinder.recommendation

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
internal interface RecommendationUserCommonFriend_PhotoDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertCommonFriend_Photo(bond: RecommendationUserCommonFriendEntity_PhotoEntity)
}
