package data.tinder.recommendation

import androidx.annotation.Px
import com.squareup.moshi.Json

internal class RecommendationUserPhotoProcessedFile(
    @field:Json(name = "width")
    @Px
    val widthPx: Int,
    @field:Json(name = "url")
    val url: String,
    @field:Json(name = "height")
    @Px
    val heightPx: Int)
