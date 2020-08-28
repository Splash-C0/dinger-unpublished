package data.tinder.login.sms

import com.squareup.moshi.Json

internal class SmsVerifyOneTimePasswordResponse private constructor(
    @field:Json(name = "data") val data: SmsVerifyOneTimePasswordResponseData)
