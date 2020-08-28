package data.tinder.recommendation

import com.squareup.moshi.Json

internal class RecommendationUserJobCompany(
    @field:Json(name = "name")
    val name: String)
