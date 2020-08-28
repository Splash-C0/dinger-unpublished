package data.tinder.recommendation

import androidx.room.Embedded
import androidx.room.Relation

internal class RecommendationUserCommonFriendWithRelatives(
    @Embedded
    var recommendationUserCommonFriend: RecommendationUserCommonFriendEntity,
    @Relation(parentColumn = "id",
        entityColumn = "recommendationUserCommonFriendEntityId",
        entity = RecommendationUserCommonFriendEntity_PhotoEntity::class,
        projection = ["recommendationUserCommonFriendPhotoEntitySmall"])
    var photos: Set<String>) {
  constructor() : this(RecommendationUserCommonFriendEntity.NONE, emptySet())
}
