package data.tinder.profile

import com.squareup.moshi.Json

internal class ProfileJobTitle private constructor(
    @field:Json(name = "displayed")
    val displayed: Boolean,
    @field:Json(name = "name")
    val name: String)
