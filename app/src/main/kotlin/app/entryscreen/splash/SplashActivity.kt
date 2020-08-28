package app.entryscreen.splash

import android.content.Intent
import android.content.IntentSender
import android.content.safeApplication
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import app.MainApplication
import app.entryscreen.tutorial.tutorialActivityCallingIntent
import app.home.HomeActivity
import app.settings.EXTRA_KEY_CLICK_PREFERENCE
import app.taskDescriptionFactory
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import iaps.PurchaseManager
import iaps.SKU_ACTIVATION_STATE_ACTIVATED
import org.stoyicker.dinger.R
import reporter.CrashReporter
import javax.inject.Inject
import kotlin.system.exitProcess

/**
 * A simple activity that acts as a splash screen.
 *
 * Note how, instead of using the content view to set the splash, we just set the splash as
 * background in the theme. This allows it to be shown without having to wait for the content view
 * to be drawn.
 */
internal class SplashActivity :
    LoggedInCheckCoordinator.ResultCallback,
    AppCompatActivity() {
  @Inject
  lateinit var loggedInCheckCoordinator: LoggedInCheckCoordinator
  @Inject
  lateinit var crashReporter: CrashReporter
  private lateinit var loggedInCheckCoordinatorRunnable: Runnable
  private var handler: Handler? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_splash)
    setTaskDescription(taskDescriptionFactory(this))
    inject()
    loggedInCheckCoordinatorRunnable = Runnable { loggedInCheckCoordinator.actionRun() }
  }

  override fun onDestroy() {
    loggedInCheckCoordinator.actionCancel()
    super.onDestroy()
  }

  override fun onResume() {
    super.onResume()
    updateOrStart()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    when (requestCode) {
      REQUEST_CODE_APP_UPDATE ->
        if (resultCode == RESULT_OK) {
          scheduleContentOpening()
        } else {
          updateOrStart()
        }
      else -> throw IllegalStateException(
          "Illegal request code in $localClassName#onActivityResult: $requestCode")
    }
  }

  override fun onPause() {
    handler?.removeCallbacksAndMessages(null)
    super.onPause()
  }

  override fun onLoggedInUserFound() = continueLoggedIn()

  override fun onLoggedInUserNotFound() = startTutorial()

  /**
   * Schedules the app content to be shown.
   */
  private fun scheduleContentOpening() {
    handler = Handler().apply {
      postDelayed(loggedInCheckCoordinatorRunnable, SHOW_TIME_MILLIS)
    }
  }

  private fun inject() = safeApplication<MainApplication>().entryScreenComponent
      .newSplashComponentFactory()
      .create(
          activity = this,
          loggedInCheckResultCallback = this)
      .inject(this)

  private fun startTutorial() {
    tutorialActivityCallingIntent(this).apply {
      flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
      startActivity(this)
    }
    supportFinishAfterTransition()
  }

  private fun continueLoggedIn() {
    HomeActivity.getCallingIntent(this).apply {
      flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
      if (intent.hasExtra(EXTRA_KEY_CLICK_PREFERENCE)
          || PurchaseManager.getAutoswipeSubscriptionState() != SKU_ACTIVATION_STATE_ACTIVATED) {
        putExtra(EXTRA_KEY_CLICK_PREFERENCE, intent.getIntExtra(EXTRA_KEY_CLICK_PREFERENCE, 0))
      }
      startActivity(this)
    }
    supportFinishAfterTransition()
  }

  private fun updateOrStart() =
      with(AppUpdateManagerFactory.create(this)) {
        appUpdateInfo.apply {
          addOnFailureListener {
            crashReporter.report(it)
            scheduleContentOpening()
          }
          addOnSuccessListener { appUpdateInfo ->
            when (appUpdateInfo.updateAvailability()) {
              UpdateAvailability.UPDATE_AVAILABLE,
              UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS ->
                if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                  try {
                    startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        this@SplashActivity,
                        REQUEST_CODE_APP_UPDATE)
                  } catch (exception: IntentSender.SendIntentException) {
                    // According to the source of Activity, the result of the
                    // started activity intent sender was ActivityManager.START_CANCELED
                    exitProcess(0)
                  }
                } else {
                  scheduleContentOpening()
                }
              else -> scheduleContentOpening()
            }
          }
        }
      }
}

private const val SHOW_TIME_MILLIS = 500L
private const val REQUEST_CODE_APP_UPDATE = 87
