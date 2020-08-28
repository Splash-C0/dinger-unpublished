package data.tinder.recommendation

import com.squareup.moshi.Json

internal class RecommendationResponse private constructor(
    @field:Json(name = "status")
    val status: Int,
    @field:Json(name = "results")
    val recommendations: Array<Recommendation>?,
    @field:Json(name = "message")
    val message: String?)
