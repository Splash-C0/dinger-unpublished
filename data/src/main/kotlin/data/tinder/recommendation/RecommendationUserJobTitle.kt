package data.tinder.recommendation

import com.squareup.moshi.Json

internal class RecommendationUserJobTitle(
    @field:Json(name = "name")
    val name: String)
