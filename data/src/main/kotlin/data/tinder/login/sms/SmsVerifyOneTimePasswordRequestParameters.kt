package data.tinder.login.sms

import com.squareup.moshi.Json

internal class SmsVerifyOneTimePasswordRequestParameters (
    @field:Json(name = "phone_number")
    private val phoneNumber: String,
    @field:Json(name = "otp_code")
    private val otp: String)
