package data.tinder.recommendation

import com.squareup.moshi.Json
import java.util.Date

internal class RecommendationUserInstagram private constructor(
    @field:Json(name = "profile_picture")
    val profilePictureUrl: String,
    @field:Json(name = "last_fetch_time")
    val lastFetchTime: Date?,
    @field:Json(name = "media_count")
    val mediaCount: Int,
    @field:Json(name = "photos")
    val photos: Array<RecommendationUserInstagramPhoto>?,
    @field:Json(name = "completed_initial_fetch")
    val completedInitialFetch: Boolean,
    @field:Json(name = "username")
    val username: String)
