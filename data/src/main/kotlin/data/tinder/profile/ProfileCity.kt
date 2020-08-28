package data.tinder.profile

import com.squareup.moshi.Json

internal class ProfileCity private constructor(
    @field:Json(name = "name")
    val name: String,
    @field:Json(name = "bounds")
    val bounds: ProfileBounds)
