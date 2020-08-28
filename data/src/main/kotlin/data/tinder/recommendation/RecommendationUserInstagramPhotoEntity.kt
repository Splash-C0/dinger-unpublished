package data.tinder.recommendation

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index("link")])
internal class RecommendationUserInstagramPhotoEntity constructor(
    @PrimaryKey
    var link: String,
    var imageUrl: String,
    var thumbnailUrl: String,
    var ts: String)
