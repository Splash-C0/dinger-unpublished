package data.tinder.login.sms

import com.squareup.moshi.Json

internal class SmsVerifyOneTimePasswordResponseData private constructor(
    @field:Json(name = "refresh_token") val refreshToken: String,
    @field:Json(name = "onboarding_token") val onboardingToken: String,
    @field:Json(name = "api_token") val apiToken: String,
    @field:Json(name = "requires_relogin") val requiresLogin: Boolean,
    @field:Json(name = "validated") val validated: Boolean)
