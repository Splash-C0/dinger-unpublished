package domain.login

data class DomainAuthenticatedUser(val apiKey: String?, val isNewUser: Boolean, val refreshToken: String?)
