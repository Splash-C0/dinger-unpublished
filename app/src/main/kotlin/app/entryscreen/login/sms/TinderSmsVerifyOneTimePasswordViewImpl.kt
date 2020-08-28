package app.entryscreen.login.sms

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.ContentLoadingProgressBar
import org.stoyicker.dinger.R
import otpview.OtpView
import otpview.SimpleOtpInputListener

internal class TinderSmsVerifyOneTimePasswordViewImpl(
    private val otp: OtpView,
    private val progress: ContentLoadingProgressBar) : TinderSmsVerifyOneTimePasswordView {
  override fun setRunning() {
    otp.isEnabled = false
    progress.show()
  }

  override fun setStale() {
    otp.isEnabled = true
    progress.hide()
  }

  override fun setError(error: Throwable) {
    otp.apply {
      setText("")
      requestFocusFirstBlankBox()
      (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
          .toggleSoftInputFromWindow(
              applicationWindowToken,
              InputMethodManager.SHOW_IMPLICIT,
              0);
    }
    setStale()
    Toast.makeText(otp.context, R.string.phone_number_unavailable, Toast.LENGTH_LONG).show()
  }

  override fun setOtpLength(otpLength: Int) {
    otp.boxAmount = otpLength
  }

  override fun setOtpViewListener(f: (String) -> Unit) {
    otp.registerOtpInputListener(object : SimpleOtpInputListener() {
      override fun onFullInput(p0: String?) {
        f(p0!!)
      }
    })
  }
}
