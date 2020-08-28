package data.tinder.profile

import com.squareup.moshi.Json

internal class ProfileCountry private constructor(
    @field:Json(name = "name")
    val name: String,
    @field:Json(name = "cc")
    val countryCode: String,
    @field:Json(name = "bounds")
    val bounds: ProfileBounds)
