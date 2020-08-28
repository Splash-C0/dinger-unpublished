package data.tinder.recommendation

import androidx.room.Embedded
import androidx.room.Relation

internal class RecommendationUserWithRelatives(
    @Embedded
    var recommendationUserEntity: RecommendationUserEntity,
    @Relation(parentColumn = "id", entityColumn = "recommendationUserEntityId",
        entity = RecommendationUserEntity_RecommendationUserCommonFriendEntity::class,
        projection = ["recommendationUserCommonFriendEntityId"])
    var commonFriends: Set<String>,
    @Relation(parentColumn = "id", entityColumn = "recommendationUserEntityId",
        entity = RecommendationUserEntity_RecommendationLikeEntity::class,
        projection = ["recommendationLikeEntityId"])
    var commonLikes: Set<String>,
    @Relation(parentColumn = "id", entityColumn = "recommendationUserEntityId",
        entity = RecommendationUserEntity_RecommendationUserPhotoEntity::class,
        projection = ["recommendationUserPhotoEntityId"])
    var photos: Set<String>,
    @Relation(parentColumn = "id", entityColumn = "recommendationUserEntityId",
        entity = RecommendationUserEntity_RecommendationUserJobEntity::class,
        projection = ["recommendationUserJobEntityId"])
    var jobs: Set<String>,
    @Relation(parentColumn = "id", entityColumn = "recommendationUserEntityId",
        entity = RecommendationUserEntity_RecommendationUserSchoolEntity::class,
        projection = ["recommendationUserSchoolEntityName"])
    var schools: Set<String>,
    @Relation(parentColumn = "id", entityColumn = "recommendationUserEntityId",
        entity = RecommendationUserEntity_RecommendationUserTeaserEntity::class,
        projection = ["recommendationUserTeaserEntityId"])
    var teasers: Set<String>) {
  constructor() : this(
      RecommendationUserEntity.NONE,
      commonFriends = emptySet(),
      commonLikes = emptySet(),
      photos = emptySet(),
      jobs = emptySet(),
      schools = emptySet(),
      teasers = emptySet())
}
