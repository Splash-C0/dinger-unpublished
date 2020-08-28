package data.tinder.recommendation

import androidx.room.Entity
import androidx.room.Index

@Entity(indices = [Index("recommendationUserEntityId")],
    primaryKeys = ["recommendationUserEntityId", "recommendationUserSchoolEntityName"])
internal class RecommendationUserEntity_RecommendationUserSchoolEntity(
    var recommendationUserEntityId: String,
    var recommendationUserSchoolEntityName: String)
