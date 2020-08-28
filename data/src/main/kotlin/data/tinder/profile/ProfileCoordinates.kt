package data.tinder.profile

import com.squareup.moshi.Json

internal class ProfileCoordinates private constructor(
    @field:Json(name = "lat")
    val lat: Double,
    @field:Json(name = "lng")
    val lng: Double)
