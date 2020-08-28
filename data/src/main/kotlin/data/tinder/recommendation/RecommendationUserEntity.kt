package data.tinder.recommendation

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(indices = [Index("id"),
  Index("name"),
  Index("instagram"),
  Index("teaser"),
  Index("spotifyThemeTrack")],
    foreignKeys = [
      ForeignKey(entity = RecommendationUserInstagramEntity::class,
          parentColumns = ["username"],
          childColumns = ["instagram"]),
      ForeignKey(entity = RecommendationUserTeaserEntity::class,
          parentColumns = ["id"],
          childColumns = ["teaser"]),
      ForeignKey(entity = RecommendationUserSpotifyThemeTrackEntity::class,
          parentColumns = ["id"],
          childColumns = ["spotifyThemeTrack"])])
internal open class RecommendationUserEntity(
    @ColumnInfo(collate = ColumnInfo.NOCASE)
    var bio: String?,
    var distanceMiles: Int,
    var commonFriendCount: Int,
    var commonLikeCount: Int,
    var contentHash: String?,
    @PrimaryKey
    var id: String,
    @Embedded
    var birthDate: Date?,
    @ColumnInfo(collate = ColumnInfo.NOCASE)
    var name: String,
    @ColumnInfo(collate = ColumnInfo.NOCASE)
    var instagram: String?,
    @ColumnInfo(collate = ColumnInfo.NOCASE)
    var teaser: String?,
    var sNumber: Long,
    var spotifyThemeTrack: String?,
    var gender: Int,
    @ColumnInfo(collate = ColumnInfo.NOCASE)
    var birthDateInfo: String,
    var groupMatched: Boolean,
    var liked: Boolean = false,
    var matched: Boolean = false,
    val insertionEpoch: Long,
    val filterableContent: String?) {
  companion object {
    val NONE = RecommendationUserEntity(
        bio = null,
        distanceMiles = 0,
        commonFriendCount = 0,
        commonLikeCount = 0,
        contentHash = null,
        id = "",
        birthDate = Date(),
        name = "",
        instagram = null,
        teaser = "",
        sNumber = 0,
        spotifyThemeTrack = null,
        gender = 0,
        birthDateInfo = "",
        groupMatched = false,
        insertionEpoch = System.currentTimeMillis(),
        filterableContent = null)
  }
}
