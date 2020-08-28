package data.tinder.login.sms

import com.squareup.moshi.Json

internal class SmsLoginResponseMeta private constructor(
    @field:Json(name = "status")
    val status: Int)
