package data.account

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import domain.loggedincheck.LoggedInCheck
import domain.login.AccountManagement
import org.stoyicker.dinger.data.R

internal class AppAccountAuthenticator(context: Context)
  : AccountManagement,
    LoggedInCheck,
    AbstractAccountAuthenticator(context) {
  private val delegate by lazy { AccountManager.get(context) }

  init {
    ACCOUNT_TYPE = context.getString(R.string.account_type)
  }

  override fun addAccount(
      p0: AccountAuthenticatorResponse?,
      p1: String?,
      p2: String?,
      p3: Array<out String>?,
      p4: Bundle?) = throw UnsupportedOperationException("Not supported")

  override fun getAuthTokenLabel(p0: String?) =
      throw UnsupportedOperationException("Not supported")

  override fun confirmCredentials(
      p0: AccountAuthenticatorResponse?,
      p1: Account?,
      p2: Bundle?) = throw UnsupportedOperationException("Not supported")

  override fun updateCredentials(
      p0: AccountAuthenticatorResponse?,
      p1: Account?,
      p2: String?,
      p3: Bundle?) = throw UnsupportedOperationException("Not supported")

  override fun getAuthToken(
      p0: AccountAuthenticatorResponse?,
      p1: Account?,
      p2: String?,
      p3: Bundle?) = throw UnsupportedOperationException("Not supported")

  override fun hasFeatures(
      p0: AccountAuthenticatorResponse?,
      p1: Account?,
      p2: Array<out String>?) = throw UnsupportedOperationException("Not supported")

  override fun editProperties(p0: AccountAuthenticatorResponse?, p1: String?) =
      throw UnsupportedOperationException("Not supported")

  override fun setAccount(accountId: String, tinderApiKey: String?, refreshToken: String?): Boolean {
    removeAccounts()
    return updateOrAddAccount(accountId, tinderApiKey, refreshToken, ACCOUNT_TYPE)
  }

  private fun updateOrAddAccount(
      id: String,
      tinderApiKey: String?,
      refreshToken: String?,
      accountType: String): Boolean {
    return Account(id, accountType).let {
      if (delegate.addAccountExplicitly(it, tinderApiKey, Bundle().apply { putString(EXTRA_KEY_REFRESH_TOKEN, refreshToken) })) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          delegate.notifyAccountAuthenticated(it)
        }
        true
      } else {
        false
      }
    }
  }

  override fun removeAccounts() = with(delegate) {
    getAccountsByType(ACCOUNT_TYPE).forEach { removeAccountExplicitly(it) }
  }

  override fun isThereALoggedInUser() = getApiToken() != null || getRefreshToken() != null

  fun getAccountId() = delegate.getAccountsByType(ACCOUNT_TYPE).let {
    when (it.size) {
      0 -> null
      else -> it.first().name
    }
  }

  fun getApiToken() = delegate.getAccountsByType(ACCOUNT_TYPE).let {
    when (it.size) {
      0 -> null
      else -> delegate.getPassword(it.first())
    }
  }

  fun getRefreshToken() = delegate.getAccountsByType(ACCOUNT_TYPE).let {
    when (it.size) {
      0 -> null
      else -> delegate.getUserData(it.first(), EXTRA_KEY_REFRESH_TOKEN)
    }
  }
}

private lateinit var ACCOUNT_TYPE: String
private const val EXTRA_KEY_REFRESH_TOKEN = "EXTRA_KEY_REFRESH_TOKEN"
