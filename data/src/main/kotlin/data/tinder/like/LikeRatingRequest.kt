package data.tinder.like

import com.squareup.moshi.Json

internal class LikeRatingRequest(
    @field:Json(name = "content_hash")
    val contentHash: String?,
    @field:Json(name = "super")
    val didRecUserSuperLike: Int = 1,
    @field:Json(name = "is_boosting")
    val isCurrentUserBoosting: Boolean = true,
    @field:Json(name = "rec_traveling")
    val isCurrentUserPassporting: Boolean = true,
    @field:Json(name = "fast_match")
    val isFastMatch: Boolean = true,
    @field:Json(name = "top_picks")
    val isTopPicks: Boolean,
    @field:Json(name = "undo")
    val isUndo: Boolean = false,
    @field:Json(name = "photoId")
    val photoId: String,
    @field:Json(name = "placeId")
    val placeId: String,
    @field:Json(name = "s_number")
    val sNumber: Long,
    @field:Json(name = "user_traveling")
    val wasRecUserPassporting: Boolean = false)
