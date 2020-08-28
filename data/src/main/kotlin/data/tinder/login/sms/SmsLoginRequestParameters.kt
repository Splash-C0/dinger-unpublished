package data.tinder.login.sms

import com.squareup.moshi.Json
import org.stoyicker.dinger.data.BuildConfig

internal class SmsLoginRequestParameters(
    @field:Json(name = "id")
    private val phoneNumber: String,
    @field:Json(name = "refresh_token")
    private val refreshToken: String,
    @field:Json(name = "client_version")
    private val clientVersion: String = BuildConfig.TINDER_VERSION_NAME)
