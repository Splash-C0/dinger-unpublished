package data.tinder.recommendation

import androidx.room.Entity
import androidx.room.Index

@Entity(indices = [Index("recommendationUserEntityId")],
    primaryKeys = ["recommendationUserEntityId", "recommendationUserCommonFriendEntityId"])
internal class RecommendationUserEntity_RecommendationUserCommonFriendEntity(
    var recommendationUserEntityId: String,
    var recommendationUserCommonFriendEntityId: String)
