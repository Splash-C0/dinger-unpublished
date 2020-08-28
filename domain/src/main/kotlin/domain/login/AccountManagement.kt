package domain.login

interface AccountManagement {
  fun setAccount(accountId: String, tinderApiKey: String? = null, refreshToken: String? = null): Boolean

  fun removeAccounts()
}
