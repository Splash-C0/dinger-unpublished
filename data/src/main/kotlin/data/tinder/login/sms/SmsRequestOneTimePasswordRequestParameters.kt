package data.tinder.login.sms

import com.squareup.moshi.Json

internal class SmsRequestOneTimePasswordRequestParameters (
    @field:Json(name = "phone_number")
    private val phoneNumber: String)
