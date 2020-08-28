package domain.login

object AccountManagementHolder {
  internal lateinit var accountManagement: AccountManagement

  fun accountManagement(it: AccountManagement) {
    accountManagement = it
  }
}
