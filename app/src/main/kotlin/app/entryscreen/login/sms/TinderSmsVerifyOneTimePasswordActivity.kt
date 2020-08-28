package app.entryscreen.login.sms

import android.content.Context
import android.content.Intent
import android.content.safeApplication
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.MainApplication
import kotlinx.android.synthetic.main.activity_verify_otp.otp
import kotlinx.android.synthetic.main.activity_verify_otp.progress
import org.stoyicker.dinger.R
import javax.inject.Inject

internal class TinderSmsVerifyOneTimePasswordActivity
  : AppCompatActivity(), TinderSmsVerifyOneTimePasswordCoordinator.ResultCallback {
  @Inject
  lateinit var tinderVerifySmsOneTimePasswordCoordinator: TinderSmsVerifyOneTimePasswordCoordinator

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_verify_otp)
    inject()
    tinderVerifySmsOneTimePasswordCoordinator.bind(
        intent.getStringExtra(EXTRA_KEY_PHONE_NUMBER)!!,
        intent.getIntExtra(EXTRA_KEY_OTP_LENGTH, -1))
    otp.requestFocusFirstBlankBox()
  }

  override fun onDestroy() {
    super.onDestroy()
    tinderVerifySmsOneTimePasswordCoordinator.release()
  }

  private fun inject() = safeApplication<MainApplication>().entryScreenComponent
      .newTinderSmsLoginVerifyOneTimePasswordComponentFactory()
      .create(otp, progress, this)
      .inject(this)

  override fun onSmsOneTimePasswordVerified() {
    setResult(RESULT_OK, null)
    supportFinishAfterTransition()
  }
}

internal fun tinderSmsVerifyOneTimePasswordActivityIntent(
    context: Context, phoneNumber: String, otpLength: Int) =
    Intent(context, TinderSmsVerifyOneTimePasswordActivity::class.java).apply {
      putExtra(EXTRA_KEY_PHONE_NUMBER, phoneNumber)
      putExtra(EXTRA_KEY_OTP_LENGTH, otpLength)
    }

private const val EXTRA_KEY_OTP_LENGTH = "EXTRA_KEY_OTP_LENGTH"
private const val EXTRA_KEY_PHONE_NUMBER = "EXTRA_KEY_PHONE_NUMBER"
