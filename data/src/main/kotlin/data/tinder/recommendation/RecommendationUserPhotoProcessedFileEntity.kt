package data.tinder.recommendation

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index("url")])
internal class RecommendationUserPhotoProcessedFileEntity(
    var widthPx: Int,
    @PrimaryKey
    var url: String,
    var heightPx: Int)
