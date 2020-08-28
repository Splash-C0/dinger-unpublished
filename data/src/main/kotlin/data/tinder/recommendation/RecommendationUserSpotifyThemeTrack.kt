package data.tinder.recommendation

import com.squareup.moshi.Json

internal class RecommendationUserSpotifyThemeTrack private constructor(
    @field:Json(name = "artists")
    val artists: Array<RecommendationUserSpotifyThemeTrackArtist>,
    @field:Json(name = "album")
    val album: RecommendationUserSpotifyThemeTrackAlbum,
    @field:Json(name = "preview_url")
    val previewUrl: String?,
    @field:Json(name = "name")
    val name: String,
    @field:Json(name = "id")
    val id: String,
    @field:Json(name = "uri")
    val uri: String)
