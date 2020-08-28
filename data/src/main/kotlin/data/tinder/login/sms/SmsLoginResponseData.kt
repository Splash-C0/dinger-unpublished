package data.tinder.login.sms

import com.squareup.moshi.Json

internal class SmsLoginResponseData private constructor(
    @field:Json(name = "is_new_user")
    val isNewUser: Boolean,
    @field:Json(name = "api_token")
    val apiToken: String,
    @field:Json(name = "refresh_token")
    val refreshToken: String)
