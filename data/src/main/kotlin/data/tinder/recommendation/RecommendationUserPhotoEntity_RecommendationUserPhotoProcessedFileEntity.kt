package data.tinder.recommendation

import androidx.room.Entity
import androidx.room.Index

@Entity(indices = [Index("recommendationUserPhotoEntityId")],
    primaryKeys = [
      "recommendationUserPhotoEntityId",
      "recommendationUserPhotoProcessedFileEntityUrl"])
internal class RecommendationUserPhotoEntity_RecommendationUserPhotoProcessedFileEntity(
    var recommendationUserPhotoEntityId: String,
    var recommendationUserPhotoProcessedFileEntityUrl: String)
