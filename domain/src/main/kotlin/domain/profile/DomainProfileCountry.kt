package domain.profile

data class DomainProfileCountry(
    val name: String,
    val countryCode: String,
    val bounds: DomainProfileBounds?)
