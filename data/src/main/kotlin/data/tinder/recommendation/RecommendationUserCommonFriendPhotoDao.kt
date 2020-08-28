package data.tinder.recommendation

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
internal interface RecommendationUserCommonFriendPhotoDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertPhoto(photo: RecommendationUserCommonFriendPhotoEntity)
}
