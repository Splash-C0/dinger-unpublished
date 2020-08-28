package domain.recommendation

enum class DomainRecommendationGender {
  GENDER_MALE, GENDER_FEMALE;

  companion object {
    fun fromGenderInt(from: Int) = DomainRecommendationGender.values()[from]
  }
}
