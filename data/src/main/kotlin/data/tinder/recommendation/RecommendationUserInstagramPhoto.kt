package data.tinder.recommendation

import com.squareup.moshi.Json

internal class RecommendationUserInstagramPhoto(
    @field:Json(name = "link")
    val link: String?,
    @field:Json(name = "image")
    val imageUrl: String,
    @field:Json(name = "thumbnail")
    val thumbnailUrl: String,
    @field:Json(name = "ts")
    val ts: String)
