package data.tinder.recommendation

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index("id"), Index("album")],
    foreignKeys = [ForeignKey(
        entity = RecommendationUserSpotifyThemeTrackAlbumEntity::class,
        parentColumns = ["id"],
        childColumns = ["album"])])
internal class RecommendationUserSpotifyThemeTrackEntity(
    var album: String?,
    var previewUrl: String?,
    var name: String,
    @PrimaryKey
    var id: String,
    var uri: String) {
  companion object {
    val NONE = RecommendationUserSpotifyThemeTrackEntity(album = "",
        previewUrl = null,
        name = "",
        id = "",
        uri = "")
  }
}
