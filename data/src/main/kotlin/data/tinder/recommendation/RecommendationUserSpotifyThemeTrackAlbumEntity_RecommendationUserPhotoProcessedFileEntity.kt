package data.tinder.recommendation

import androidx.room.Entity
import androidx.room.Index

@Entity(indices = [Index("recommendationUserSpotifyThemeTrackAlbumEntityId")],
    primaryKeys = [
      "recommendationUserSpotifyThemeTrackAlbumEntityId",
      "recommendationUserPhotoProcessedFileEntityUrl"])
internal class
RecommendationUserSpotifyThemeTrackAlbumEntity_RecommendationUserPhotoProcessedFileEntity(
    var recommendationUserSpotifyThemeTrackAlbumEntityId: String,
    var recommendationUserPhotoProcessedFileEntityUrl: String)
