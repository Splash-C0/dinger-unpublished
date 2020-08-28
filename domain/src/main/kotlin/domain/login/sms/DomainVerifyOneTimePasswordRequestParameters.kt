package domain.login.sms

data class DomainVerifyOneTimePasswordRequestParameters(
    val phoneNumber: String,
    val otp: String)
