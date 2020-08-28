package data.tinder.like

import com.squareup.moshi.Json
import data.network.JsonObjectOrFalse

internal class LikeResponse private constructor(
    @field:Json(name = "match")
    @field:JsonObjectOrFalse()
    val match: Match?,
    @field:Json(name = "rate_limited_until")
    val rateLimitedUntil: Long?)
