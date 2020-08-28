package data.tinder.recommendation

import com.squareup.moshi.Json

internal class RecommendationUserPhoto private constructor(
    @field:Json(name = "id")
    val id: String,
    @field:Json(name = "url")
    val url: String,
    @field:Json(name = "processedFiles")
    val processedFiles: Array<RecommendationUserPhotoProcessedFile>)
