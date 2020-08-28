package domain.profile

data class DomainProfileEmailSettings(
    val newMatches: Boolean,
    val messages: Boolean,
    val promotions: Boolean)
