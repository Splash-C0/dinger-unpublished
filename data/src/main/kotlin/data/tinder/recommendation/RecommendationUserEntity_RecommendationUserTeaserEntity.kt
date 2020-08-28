package data.tinder.recommendation

import androidx.room.Entity
import androidx.room.Index

@Entity(indices = [Index("recommendationUserEntityId")],
    primaryKeys = ["recommendationUserEntityId", "recommendationUserTeaserEntityId"])
internal class RecommendationUserEntity_RecommendationUserTeaserEntity(
    var recommendationUserEntityId: String,
    var recommendationUserTeaserEntityId: String)
