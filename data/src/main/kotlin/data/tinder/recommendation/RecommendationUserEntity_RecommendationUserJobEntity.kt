package data.tinder.recommendation

import androidx.room.Entity
import androidx.room.Index

@Entity(indices = [Index("recommendationUserEntityId")],
    primaryKeys = ["recommendationUserEntityId", "recommendationUserJobEntityId"])
internal class RecommendationUserEntity_RecommendationUserJobEntity(
    var recommendationUserEntityId: String,
    var recommendationUserJobEntityId: String)
