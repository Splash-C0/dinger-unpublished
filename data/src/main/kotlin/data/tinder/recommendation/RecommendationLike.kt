package data.tinder.recommendation

import com.squareup.moshi.Json

internal class RecommendationLike private constructor(
    @field:Json(name = "id")
    val id: String,
    @field:Json(name = "name")
    val name: String)
