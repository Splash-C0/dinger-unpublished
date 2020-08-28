package data.tinder.recommendation

import androidx.room.Entity
import androidx.room.Index

@Entity(indices = [Index("recommendationUserCommonFriendEntityId")],
    primaryKeys = [
      "recommendationUserCommonFriendEntityId",
      "recommendationUserCommonFriendPhotoEntitySmall"])
internal class RecommendationUserCommonFriendEntity_PhotoEntity(
    var recommendationUserCommonFriendEntityId: String,
    var recommendationUserCommonFriendPhotoEntitySmall: String)
