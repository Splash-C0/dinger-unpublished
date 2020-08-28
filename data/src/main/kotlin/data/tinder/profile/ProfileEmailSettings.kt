package data.tinder.profile

import com.squareup.moshi.Json

internal class ProfileEmailSettings private constructor(
    @field:Json(name = "new_matches")
    val newMatches: Boolean,
    @field:Json(name = "messages")
    val messages: Boolean,
    @field:Json(name = "promotions")
    val promotions: Boolean)
