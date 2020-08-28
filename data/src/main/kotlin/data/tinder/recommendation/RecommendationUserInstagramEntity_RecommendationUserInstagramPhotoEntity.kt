package data.tinder.recommendation

import androidx.room.Entity
import androidx.room.Index

@Entity(indices = [Index("recommendationUserInstagramEntityUsername")],
    primaryKeys = [
      "recommendationUserInstagramEntityUsername",
      "recommendationUserInstagramPhotoEntityLink"])
internal class RecommendationUserInstagramEntity_RecommendationUserInstagramPhotoEntity(
    var recommendationUserInstagramEntityUsername: String,
    var recommendationUserInstagramPhotoEntityLink: String)
