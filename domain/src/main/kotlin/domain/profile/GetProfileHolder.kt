package domain.profile

object GetProfileHolder {
  internal lateinit var getProfile: GetProfile

  fun getProfile(it: GetProfile) {
    getProfile = it
  }
}
