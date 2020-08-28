package data.tinder.dislike

import com.squareup.moshi.Json

internal class DislikeResponse private constructor(@field:Json(name = "status") val status: Int)
