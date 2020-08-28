package app.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.content.startIntent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import app.home.me.logout.LogoutCoordinator
import domain.autoswipe.ImmediatePostAutoSwipeUseCase
import iaps.PurchaseManager
import iaps.SKU_ACTIVATION_STATE_ACTIVATED
import iaps.SKU_ACTIVATION_STATE_NOT_ACTIVATED
import iaps.SKU_AUTOSWIPE_0
import iaps.URI_COMMAND_PURCHASE
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.schedulers.Schedulers
import org.stoyicker.dinger.R
import reporter.CrashReporters
import java.lang.ref.WeakReference

internal class SettingsPreferenceFragmentCompat : PreferenceFragmentCompat() {
  private val logoutCoordinator by lazy { LogoutCoordinator(context!!) }
  private var setAutoswipeToEnabledIfSubscriptionIsActive = false
  private var rootKey: String? = null

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    this.rootKey = rootKey
    ensureAutoswipeIsNotEnabledIfSubscriptionIsInactive()
    setPreferencesFromResource(R.xml.prefs_settings, rootKey)
    addTriggerToAutoswipeEnabledPreference()
    initializeAutoswipeRunNowPreference()
    initializeManageNotificationsPreference()
    initializeLogoutPreference()
    initializeSharePreference()
    initializeAboutTheAppPreference()
    initializePrivacyPolicyPreference()
    clickEntryIfNeedTo()
  }

  override fun onResume() {
    super.onResume()
    if (setAutoswipeToEnabledIfSubscriptionIsActive) {
      setAutoswipeToEnabledIfSubscriptionIsActive = false
      if (PurchaseManager.getAutoswipeSubscriptionState() == SKU_ACTIVATION_STATE_ACTIVATED) {
        (findPreference<Preference>(context!!.getString(R.string.preference_key_autoswipe_enabled))
            as SwitchPreferenceCompat).isChecked = true
        ImmediatePostAutoSwipeUseCase(context!!, Schedulers.trampoline()).execute(
            object : DisposableCompletableObserver() {
              override fun onComplete() {
              }

              override fun onError(error: Throwable) {
              }
            })
      } else {
        PreferenceManager.getDefaultSharedPreferences(context!!).edit()
            .putBoolean(context!!.getString(R.string.preference_key_autoswipe_enabled), false)
            .apply()
      }
    }
  }

  private fun ensureAutoswipeIsNotEnabledIfSubscriptionIsInactive() {
    if (PurchaseManager.getAutoswipeSubscriptionState() != SKU_ACTIVATION_STATE_ACTIVATED) {
      PreferenceManager.getDefaultSharedPreferences(context!!)
          .edit()
          .putBoolean(context!!.getString(R.string.preference_key_autoswipe_enabled), false)
          .putBoolean(context!!.getString(R.string.preference_key_autoswipe_enabled_backup), false)
          .apply()
    }
  }

  private fun autoswipeOrTriggerPurchase(): Boolean {
    val autoSwipeSubscriptionState = PurchaseManager.getAutoswipeSubscriptionState()
    if (autoSwipeSubscriptionState != SKU_ACTIVATION_STATE_ACTIVATED) {
      if (autoSwipeSubscriptionState == SKU_ACTIVATION_STATE_NOT_ACTIVATED) {
        setAutoswipeToEnabledIfSubscriptionIsActive = true
        PurchaseManager.activityForPurchaseFlow = WeakReference(requireActivity())
        context!!.contentResolver
            .query(
                URI_COMMAND_PURCHASE.buildUpon()
                    .appendPath(SKU_AUTOSWIPE_0)
                    .build(),
                emptyArray(),
                null,
                null,
                null)?.close()
      }
      return false
    } else {
      ImmediatePostAutoSwipeUseCase(context!!, Schedulers.trampoline()).execute(
          object : DisposableCompletableObserver() {
            override fun onComplete() {
            }

            override fun onError(error: Throwable) {
              CrashReporters.bugsnag().report(error)
              ImmediatePostAutoSwipeUseCase(context!!, Schedulers.trampoline())
                  .execute(this)
            }
          })
      return true
    }
  }

  @SuppressLint("Recycle") // Addressed
  private fun addTriggerToAutoswipeEnabledPreference() {
    findPreference<Preference>(context!!.getString(R.string.preference_key_autoswipe_enabled))?.onPreferenceChangeListener =
        Preference.OnPreferenceChangeListener { _, value ->
          if (value is Boolean && value) {
            autoswipeOrTriggerPurchase()
          } else {
            true
          }
        }
  }

  private fun initializeAutoswipeRunNowPreference() {
    findPreference<Preference>(context!!.getString(R.string.preference_key_run_now))?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
      if (PurchaseManager.getAutoswipeSubscriptionState() == SKU_ACTIVATION_STATE_ACTIVATED) {
        // This by itself will trigger an immediate run if the pref is disabled, but not otherwise,
        // so it is not redundant.
        (findPreference<Preference>(context!!.getString(R.string.preference_key_autoswipe_enabled))
            as SwitchPreferenceCompat).isChecked = true
      }
      autoswipeOrTriggerPurchase()
    }
  }

  @SuppressLint("InlinedApi")
  private fun initializeManageNotificationsPreference() {
    findPreference<Preference>(context!!.getString(R.string.preference_key_manage_notifications))?.onPreferenceClickListener =
        Preference.OnPreferenceClickListener {
          context?.startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
              .putExtra("app_package", context?.packageName)
              .putExtra("app_uid", context?.applicationInfo?.uid)
              .putExtra("android.provider.extra.APP_PACKAGE", context?.packageName))
          true
        }
  }

  private fun initializeLogoutPreference() {
    findPreference<Preference>(context!!.getString(R.string.preference_key_logout))?.onPreferenceClickListener =
        Preference.OnPreferenceClickListener {
          logoutCoordinator.actionRun()
          true
        }
  }

  private fun initializeSharePreference() {
    findPreference<Preference>(context!!.getString(R.string.preference_key_share))?.onPreferenceClickListener =
        Preference.OnPreferenceClickListener {
          val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            @SuppressLint("InlinedApi")
            flags += Intent.FLAG_ACTIVITY_NEW_DOCUMENT
            putExtra(Intent.EXTRA_TEXT, context?.getString(
                R.string.url_google_play_entry))
            putExtra(Intent.EXTRA_SUBJECT, context?.getString(R.string.app_label))
          }
          context?.startIntent(Intent.createChooser(
              intent, getString(R.string.action_share_title)))
          true
        }
  }

  private fun initializePrivacyPolicyPreference() {
    findPreference<Preference>(context!!.getString(R.string.preference_key_privacy_policy))?.onPreferenceClickListener =
        Preference.OnPreferenceClickListener {
          context?.startIntent(Intent(
              Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_privacy_policy))))
          true
        }
  }

  private fun initializeAboutTheAppPreference() {
    findPreference<Preference>(context!!.getString(R.string.preference_key_about_the_app))?.onPreferenceClickListener =
        Preference.OnPreferenceClickListener {
          context?.startIntent(Intent(
              Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_google_play_entry))))
          true
        }
  }

  @SuppressLint("RestrictedApi")
  private fun clickEntryIfNeedTo() = activity?.intent?.getIntExtra(EXTRA_KEY_CLICK_PREFERENCE, 0)?.let {
    activity?.intent?.removeExtra(EXTRA_KEY_CLICK_PREFERENCE)
    when (it) {
      0 -> Unit
      R.string.preference_key_autoswipe_enabled ->
        if (!PreferenceManager.getDefaultSharedPreferences(context!!)
                .getBoolean(
                    context!!.getString(R.string.preference_key_autoswipe_enabled), PreferenceManager.getDefaultSharedPreferences(context!!)
                    .getBoolean(
                        context!!.getString(R.string.preference_key_autoswipe_enabled_backup), false))) {
          findPreference<Preference>(context!!.getString(R.string.preference_key_autoswipe_enabled))
              ?.performClick()
        }
      else -> throw UnsupportedOperationException("Unsupported click on preference ${context!!.getString(it)}")
    }
  }

  companion object {
    const val FRAGMENT_TAG = "SettingsPreferenceFragmentCompat"
  }
}

internal const val EXTRA_KEY_CLICK_PREFERENCE = "EXTRA_KEY_CLICK_PREFERENCE"
