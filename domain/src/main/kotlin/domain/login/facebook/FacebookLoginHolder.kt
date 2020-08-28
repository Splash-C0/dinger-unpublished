package domain.login.facebook

object FacebookLoginHolder {
  internal lateinit var facebookLogin: FacebookLogin

  fun facebookLogin(it: FacebookLogin) {
    facebookLogin = it
  }
}
