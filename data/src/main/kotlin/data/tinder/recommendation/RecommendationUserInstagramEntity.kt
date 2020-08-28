package data.tinder.recommendation

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(indices = [Index("username")])
internal open class RecommendationUserInstagramEntity(
    var profilePictureUrl: String?,
    @Embedded
    var lastFetchTime: Date?,
    var mediaCount: Int,
    var completedInitialFetch: Boolean,
    @PrimaryKey
    var username: String) {
  companion object {
    val NONE = RecommendationUserInstagramEntity(
        profilePictureUrl = null,
        lastFetchTime = Date(),
        mediaCount = 0,
        completedInitialFetch = false,
        username = "")
  }
}
