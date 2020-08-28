package data.tinder.recommendation

import com.squareup.moshi.Json

internal class RecommendationUserCommonFriendPhoto(
    @field:Json(name = "small")
    var small: String,
    @field:Json(name = "medium")
    var medium: String,
    @field:Json(name = "large")
    var large: String)
