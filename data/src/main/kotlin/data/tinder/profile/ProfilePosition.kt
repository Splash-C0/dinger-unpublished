package data.tinder.profile

import com.squareup.moshi.Json

internal class ProfilePosition private constructor(
    @field:Json(name = "at")
    val at: Long,
    @field:Json(name = "lat")
    val lat: Float,
    @field:Json(name = "lon")
    val lon: Float)
