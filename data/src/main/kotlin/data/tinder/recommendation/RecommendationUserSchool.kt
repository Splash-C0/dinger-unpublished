package data.tinder.recommendation

import com.squareup.moshi.Json

internal class RecommendationUserSchool private constructor(
    @field:Json(name = "name")
    val name: String,
    @field:Json(name = "id")
    val id: String?
)
