package data.tinder.login.sms

import com.squareup.moshi.Json

internal class SmsRequestOneTimePasswordResponseData private constructor(
    @field:Json(name = "otp_length") val otpLength: Int,
    @field:Json(name = "sms_sent") val smsSent: Boolean)
