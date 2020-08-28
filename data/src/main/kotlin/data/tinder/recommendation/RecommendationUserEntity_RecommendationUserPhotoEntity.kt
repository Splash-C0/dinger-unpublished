package data.tinder.recommendation

import androidx.room.Entity
import androidx.room.Index

@Entity(indices = [Index("recommendationUserEntityId")],
    primaryKeys = ["recommendationUserEntityId", "recommendationUserPhotoEntityId"])
internal class RecommendationUserEntity_RecommendationUserPhotoEntity(
    var recommendationUserEntityId: String,
    var recommendationUserPhotoEntityId: String)
