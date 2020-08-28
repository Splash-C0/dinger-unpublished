package data.tinder.login.facebook

import com.squareup.moshi.Json

internal class FacebookLoginResponseMeta private constructor(
    @field:Json(name = "status")
    val status: Int)
