package data.tinder.recommendation

import com.squareup.moshi.Json

internal class RecommendationUserTeaser private constructor(
    @field:Json(name = "string")
    val description: String,
    @field:Json(name = "type")
    val type: String?)
