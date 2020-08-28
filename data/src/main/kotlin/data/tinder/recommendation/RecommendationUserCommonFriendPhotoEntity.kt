package data.tinder.recommendation

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index("small")])
internal class RecommendationUserCommonFriendPhotoEntity(
    @PrimaryKey
    var small: String,
    var medium: String,
    var large: String)
