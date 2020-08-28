package data.tinder.recommendation

import androidx.room.Embedded
import androidx.room.Relation

internal class RecommendationUserInstagramWithRelatives(
    @Embedded
    var recommendationUserInstagram: RecommendationUserInstagramEntity,
    @Relation(parentColumn = "username",
        entityColumn = "recommendationUserInstagramEntityUsername",
        entity =
        RecommendationUserInstagramEntity_RecommendationUserInstagramPhotoEntity::class,
        projection = ["recommendationUserInstagramPhotoEntityLink"])
    var photos: Set<String>) {
  constructor() : this(RecommendationUserInstagramEntity.NONE, emptySet())
}
