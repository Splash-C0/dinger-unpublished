package data.tinder.recommendation

import com.squareup.moshi.Json

internal class RecommendationUserSpotifyThemeTrackAlbum private constructor(
    @field:Json(name = "images")
    val images: Array<RecommendationUserPhotoProcessedFile>?,
    @field:Json(name = "name")
    val name: String,
    @field:Json(name = "id")
    val id: String)
