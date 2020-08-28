package domain.login.sms

data class DomainSmsAuthRequestParameters(
    val phoneNumber: String,
    val refreshToken: String)
