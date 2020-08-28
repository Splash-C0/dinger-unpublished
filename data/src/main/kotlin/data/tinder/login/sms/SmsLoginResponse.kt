package data.tinder.login.sms

import com.squareup.moshi.Json

internal class SmsLoginResponse private constructor(
    @field:Json(name = "meta")
    val meta: SmsLoginResponseMeta,
    @field:Json(name = "data")
    val data: SmsLoginResponseData)
