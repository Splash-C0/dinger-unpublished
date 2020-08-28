package domain.loggedincheck

object LoggedInCheckHolder {
  internal lateinit var loggedInCheck: LoggedInCheck

  fun loggedInCheck(it: LoggedInCheck) {
    loggedInCheck = it
  }
}
