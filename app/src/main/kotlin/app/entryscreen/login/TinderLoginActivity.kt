package app.entryscreen.login

import android.content.Context
import android.content.Intent
import android.content.safeApplication
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.MainApplication
import app.entryscreen.login.facebook.FacebookLoginCoordinator
import app.entryscreen.login.facebook.TinderFacebookLoginCoordinator
import app.entryscreen.login.sms.REQUEST_CODE_SMS_LOGIN_REQUEST_OTP
import app.entryscreen.login.sms.TinderSmsLoginCoordinator
import app.entryscreen.login.sms.tinderSmsVerifyOneTimePasswordActivityIntent
import app.home.HomeActivity
import app.taskDescriptionFactory
import com.facebook.accountkit.ui.AccountKitActivity.EXTRA_EXCEPTION
import com.facebook.accountkit.ui.AccountKitActivity.RESULT_CODE_PHONE_FORMATTING_FAILED
import com.facebook.accountkit.ui.LoginFlowBroadcastReceiver
import domain.login.sms.DomainSmsOneTimePassword
import domain.login.sms.TinderSmsRequestOneTimePasswordUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.login_with_facebook
import kotlinx.android.synthetic.main.activity_login.login_with_sms
import kotlinx.android.synthetic.main.activity_login.progress
import org.stoyicker.dinger.R
import reporter.CrashReporter
import javax.inject.Inject


internal class TinderLoginActivity
  : FacebookLoginCoordinator.ResultCallback,
    TinderLoginResultCallback,
    AppCompatActivity() {
  @Inject
  lateinit var facebookLoginCoordinator: FacebookLoginCoordinator

  @Inject
  lateinit var tinderFacebookLoginCoordinator: TinderFacebookLoginCoordinator

  @Inject
  lateinit var tinderSmsLoginCoordinator: TinderSmsLoginCoordinator

  @Inject
  lateinit var crashReporter: CrashReporter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)
    setTaskDescription(taskDescriptionFactory(this))
    inject()
    facebookLoginCoordinator.bind()
    tinderSmsLoginCoordinator.bind()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    when (requestCode) {
      REQUEST_CODE_SMS_LOGIN_REQUEST_OTP ->
        if (resultCode == RESULT_OK) {
          val phoneNumber = data!!.getStringExtra(LoginFlowBroadcastReceiver.EXTRA_PHONE_NUMBER)!!
          TinderSmsRequestOneTimePasswordUseCase(
              phoneNumber.filter { it.isDigit() }, Schedulers.io(), AndroidSchedulers.mainThread()).execute(object : DisposableSingleObserver<DomainSmsOneTimePassword>() {
            override fun onSuccess(t: DomainSmsOneTimePassword) {
              if (!t.smsSent) {
                  Toast.makeText(
                      this@TinderLoginActivity,
                      R.string.error_tinder_sms_login,
                      Toast.LENGTH_SHORT)
                      .show();
                  tinderSmsLoginCoordinator.trigger()
              } else {
                  startActivityForResult(
                      tinderSmsVerifyOneTimePasswordActivityIntent(
                          this@TinderLoginActivity,
                          phoneNumber,
                          t.otpLength
                      ), REQUEST_CODE_SMS_LOGIN_VERIFY_OTP
                  )
              }
            }

            override fun onError(e: Throwable) {
              Toast.makeText(
                  this@TinderLoginActivity,
                  R.string.com_accountkit_phone_error_title,
                  Toast.LENGTH_SHORT)
                  .show();
              tinderSmsLoginCoordinator.trigger()
            }
          })
        } else if (resultCode == RESULT_CODE_PHONE_FORMATTING_FAILED) {
            Toast.makeText(
                this@TinderLoginActivity,
                R.string.com_accountkit_phone_error_title,
                Toast.LENGTH_SHORT)
                .show();
            crashReporter.report((data!!.getSerializableExtra(EXTRA_EXCEPTION) as Throwable))
            tinderSmsLoginCoordinator.trigger()
        }
      REQUEST_CODE_SMS_LOGIN_VERIFY_OTP ->
        if (resultCode == RESULT_OK) {
          onTinderLoginSuccess()
        }
      else -> facebookLoginCoordinator.onActivityResult(requestCode, resultCode, data)
    }
  }

  override fun onDestroy() {
    facebookLoginCoordinator.release()
    tinderFacebookLoginCoordinator.actionCancel()
    super.onDestroy()
  }

  override fun onFacebookTokenAcquired(facebookId: String, facebookToken: String) =
      tinderFacebookLoginCoordinator.actionDoLogin(facebookId, facebookToken)

  override fun onTinderLoginSuccess() {
    HomeActivity.getCallingIntent(this).apply {
      flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
      startActivity(this)
    }
    supportFinishAfterTransition()
  }

  private fun inject() = safeApplication<MainApplication>().entryScreenComponent
      .newTinderLoginComponentFactory()
      .create(
          activity = this,
          facebookLoginView = login_with_facebook,
          smsLoginView = login_with_sms,
          contentLoadingProgressBar = progress,
          facebookLoginResultCallback = this,
          tinderLoginResultCallback = this)
      .inject(this)

  companion object {
    fun getCallingIntent(context: Context) = Intent(context, TinderLoginActivity::class.java)
  }
}

internal const val REQUEST_CODE_SMS_LOGIN_VERIFY_OTP = 1078
