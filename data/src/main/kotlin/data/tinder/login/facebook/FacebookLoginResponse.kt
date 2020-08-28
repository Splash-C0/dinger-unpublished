package data.tinder.login.facebook

import com.squareup.moshi.Json

internal class FacebookLoginResponse private constructor(
    @field:Json(name = "meta")
    val meta: FacebookLoginResponseMeta,
    @field:Json(name = "data")
    val data: FacebookLoginResponseData)
