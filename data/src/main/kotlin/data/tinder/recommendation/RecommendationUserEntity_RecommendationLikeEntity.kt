package data.tinder.recommendation

import androidx.room.Entity
import androidx.room.Index

@Entity(indices = [Index("recommendationUserEntityId")],
    primaryKeys = ["recommendationUserEntityId", "recommendationLikeEntityId"])
internal class RecommendationUserEntity_RecommendationLikeEntity(
    var recommendationUserEntityId: String,
    var recommendationLikeEntityId: String)
