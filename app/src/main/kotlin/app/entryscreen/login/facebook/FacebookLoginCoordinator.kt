package app.entryscreen.login.facebook

import android.app.Activity
import android.content.Intent
import android.view.View
import app.entryscreen.login.HandlesActivityResult
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import reporter.CrashReporter

internal class FacebookLoginCoordinator(
    private val facebookLoginView: View,
    private val resultCallback: ResultCallback,
    private val crashReporter: CrashReporter) : HandlesActivityResult {
  private val callbackManager: CallbackManager = CallbackManager.Factory.create()

  fun bind() {
    facebookLoginView.setOnClickListener {
      LoginManager.getInstance().apply {
        logOut()
        // The behavior needs to be this because otherwise Facebook will realize this client's
        // signature hash does not match the specified and stop the login
        loginBehavior = LoginBehavior.WEB_VIEW_ONLY
        registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
              override fun onCancel() = Unit

              override fun onError(exception: FacebookException) = crashReporter.report(exception)

              override fun onSuccess(loginResult: LoginResult) {
                with(loginResult.accessToken) {
                  AccessToken.setCurrentAccessToken(this)
                  reportSuccess(this)
                }
              }
            })
        logIn(it.context as Activity, emptySet())
      }
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) =
      callbackManager.onActivityResult(requestCode, resultCode, data)

  fun release() = LoginManager.getInstance().unregisterCallback(callbackManager)

  private fun reportSuccess(accessToken: AccessToken) = accessToken.let {
    resultCallback.onFacebookTokenAcquired(it.userId, it.token)
  }

  internal interface ResultCallback {
    fun onFacebookTokenAcquired(facebookId: String, facebookToken: String)
  }
}
