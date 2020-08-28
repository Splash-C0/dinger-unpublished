package app.entryscreen.login.facebook

import android.app.Activity
import android.view.View
import android.widget.Toast
import androidx.core.widget.ContentLoadingProgressBar
import app.entryscreen.login.TinderLoginView
import org.stoyicker.dinger.R

internal class TinderFacebookLoginView(
    private val activity: Activity,
    private val loginTriggerFacebook: View,
    private val progress: ContentLoadingProgressBar)
  : TinderLoginView {
  override fun setRunning() {
    loginTriggerFacebook.isEnabled = false
    progress.show()
  }

  override fun setStale() {
    loginTriggerFacebook.isEnabled = true
    progress.hide()
  }

  override fun setError(error: Throwable?) {
    setStale()
    Toast.makeText(
        activity,
        R.string.login_failed
        , Toast.LENGTH_LONG).show()
  }
}
