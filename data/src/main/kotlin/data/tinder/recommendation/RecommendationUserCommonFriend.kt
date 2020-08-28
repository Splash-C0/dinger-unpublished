package data.tinder.recommendation

import com.squareup.moshi.Json

internal open class RecommendationUserCommonFriend(
    @field:Json(name = "id")
    var id: String,
    @field:Json(name = "name")
    var name: String,
    @field:Json(name = "degree")
    var degree: String,
    @field:Json(name = "photos")
    val photos: Set<RecommendationUserCommonFriendPhoto>?)
