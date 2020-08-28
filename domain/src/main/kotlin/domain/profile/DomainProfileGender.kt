package domain.profile

enum class DomainProfileGender {
  GENDER_UNSPECIFIED, GENDER_MALE, GENDER_FEMALE;

  companion object {
    fun fromGenderInt(from: Int?) = when (from) {
      0 -> GENDER_MALE
      1 -> GENDER_FEMALE
      else -> GENDER_UNSPECIFIED
    }
  }
}
