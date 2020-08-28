package data.tinder.login.sms

import com.squareup.moshi.Json

internal class SmsRequestOneTimePasswordResponse private constructor(
    @field:Json(name = "data") val data: SmsRequestOneTimePasswordResponseData)
