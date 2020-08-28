package data.tinder.recommendation

import androidx.room.Entity
import androidx.room.Index

@Entity(indices = [Index("recommendationUserSpotifyThemeTrackEntityId")],
    primaryKeys = [
      "recommendationUserSpotifyThemeTrackEntityId",
      "recommendationUserSpotifyThemeTrackArtistEntityId"])
internal class
RecommendationUserSpotifyThemeTrackEntity_RecommendationUserSpotifyThemeTrackArtistEntity(
    var recommendationUserSpotifyThemeTrackEntityId: String,
    var recommendationUserSpotifyThemeTrackArtistEntityId: String)
