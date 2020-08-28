package data.tinder.recommendation

import androidx.room.Embedded
import androidx.room.Relation

internal class RecommendationUserPhotoWithRelatives(
    @Embedded
    var recommendationUserPhotoEntity: RecommendationUserPhotoEntity,
    @Relation(parentColumn = "id", entityColumn = "recommendationUserPhotoEntityId",
        entity = RecommendationUserPhotoEntity_RecommendationUserPhotoProcessedFileEntity::class,
        projection = ["recommendationUserPhotoProcessedFileEntityUrl"])
    var processedFiles: Set<String>) {
  constructor() : this(RecommendationUserPhotoEntity.NONE, emptySet())
}
