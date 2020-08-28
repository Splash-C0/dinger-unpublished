package app.home.rate

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import androidx.annotation.MainThread
import iaps.PurchaseManager
import iaps.SKU_ACTIVATION_STATE_ACTIVATED
import org.stoyicker.dinger.R

internal class RateFeature(
    private val activity: Activity,
    private val resources: Resources,
    private val sharedPrefs: SharedPreferences) {
  private var rateDialog: Dialog? = null

  @MainThread
  fun start(inState: Bundle? = null) {
    if (
        PurchaseManager.getAutoswipeSubscriptionState() != SKU_ACTIVATION_STATE_ACTIVATED ||
        !PurchaseManager.trialPeriodOver) {
      // Discourage reviews from non-paying users who may not be satisfied with the app
      return
    }
    if (sharedPrefs.getBoolean(
            resources.getString(R.string.preference_key_rated_on_google_play), false)) {
      // Do not show the dialog if the user has already rated the app
      return
    }
    // If shown
    if (rateDialog != null) {
      // But it should not be shown
      if (inState != null && !inState.getBoolean(KEY_RATE_DIALOG_SHOWN, true)) {
        // Hide
        release()
      }
      return
    }
    // If not shown, but should be
    if (inState != null && inState.getBoolean(KEY_RATE_DIALOG_SHOWN, false)) {
      // Force show and reset the counter. This should never have happened, but just in case
      showDialog()
      return
    } else if (inState != null) {
        // If not shown, and it should not, we're restoring, do not show it
        return
    }
    showDialog()
  }

  @MainThread
  fun release(outState: Bundle? = null) {
    outState?.putBoolean(KEY_RATE_DIALOG_SHOWN, rateDialog?.isShowing ?: false)
    rateDialog?.dismiss()
    rateDialog = null
  }

  private fun showDialog() {
    rateDialog = AlertDialog.Builder(activity)
        .setTitle(resources.getString(R.string.rate_home_title,
            resources.getString(R.string.app_label)))
        .setMessage(R.string.rate_home_body)
        .setPositiveButton(android.R.string.ok) { _, _ ->
          val packageName = activity.packageName
          val uri = Uri.parse(resources.getString(R.string.market_intent_template, packageName))
          val intent = Intent(Intent.ACTION_VIEW, uri)
          intent.addFlags(
              Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
          intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
          sharedPrefs.edit()
              .putBoolean(resources.getString(R.string.preference_key_rated_on_google_play), true)
              .apply()
          try {
            activity.startActivity(intent)
          } catch (e: ActivityNotFoundException) {
            activity.startActivity(Intent(
                Intent.ACTION_VIEW,
                Uri.parse(resources.getString(R.string.market_link_template, packageName))))
          }
        }
        .setNegativeButton(R.string.later, null)
        .setCancelable(false)
        .create().apply {
          show()
        }
  }
}

private const val KEY_RATE_DIALOG_SHOWN = "KEY_RATE_DIALOG_SHOWN"
