package app.entryscreen.login.sms

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.core.content.ContextCompat
import com.facebook.accountkit.ui.AccountKitActivity
import com.facebook.accountkit.ui.AccountKitConfiguration
import com.facebook.accountkit.ui.LoginType
import com.facebook.accountkit.ui.SkinManager
import org.stoyicker.dinger.R

internal class TinderSmsLoginCoordinator(
    private val activity: Activity,
    private val smsLoginView: View) {
  fun bind() {
    smsLoginView.setOnClickListener {
      trigger()
    }
  }

  fun trigger() {
    activity.startActivityForResult(
        Intent(activity, AccountKitActivity::class.java).putExtra(
            AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
            AccountKitConfiguration.AccountKitConfigurationBuilder(
                LoginType.PHONE,
                AccountKitActivity.ResponseType.TOKEN)
                .setUIManager(SkinManager(
                    SkinManager.Skin.CONTEMPORARY,
                    ContextCompat.getColor(activity, R.color.text_primary),
                    R.drawable.color_sms_login_background,
                    SkinManager.Tint.BLACK,
                    55.toDouble()))
                .build()), REQUEST_CODE_SMS_LOGIN_REQUEST_OTP)
  }
}

internal const val REQUEST_CODE_SMS_LOGIN_REQUEST_OTP = 1077
