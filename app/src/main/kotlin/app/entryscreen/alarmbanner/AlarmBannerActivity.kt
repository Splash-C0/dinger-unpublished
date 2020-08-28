package app.entryscreen.alarmbanner

import android.content.Intent
import android.content.safeApplication
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import app.MainApplication
import app.home.HomeActivity
import app.taskDescriptionFactory
import domain.autoswipe.ImmediatePostAutoSwipeUseCase
import iaps.PurchaseManager
import iaps.SKU_ACTIVATION_STATE_ACTIVATED
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_alarm_banner.continue_button
import org.stoyicker.dinger.R
import javax.inject.Inject

internal class AlarmBannerActivity : AppCompatActivity(), ContinueCoordinator.ResultCallback {
  @Inject
  lateinit var alarmBannerCoordinator: AlarmBannerCoordinator
  @Inject
  lateinit var continueCoordinator: ContinueCoordinator

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_alarm_banner)
    setTaskDescription(taskDescriptionFactory(this))
    inject()
    PreferenceManager.getDefaultSharedPreferences(this).edit()
        .putBoolean(getString(R.string.preference_key_show_alarm_banner), false)
        .apply()
    alarmBannerCoordinator.actionDoSchedule()
    continueCoordinator.enable()
  }

  private fun inject() = safeApplication<MainApplication>().entryScreenComponent
      .newAlarmBannerComponentFactory()
      .create(
          continueView = continue_button,
          continueResultCallback = this)
      .inject(this)

  override fun onDestroy() {
    super.onDestroy()
    alarmBannerCoordinator.actionCancelSchedule()
  }

  override fun onContinueClicked() {
    if (PurchaseManager.getAutoswipeSubscriptionState() == SKU_ACTIVATION_STATE_ACTIVATED) {
      PreferenceManager.getDefaultSharedPreferences(this).edit()
          .putBoolean(getString(R.string.preference_key_autoswipe_enabled), true)
          .putBoolean(getString(R.string.preference_key_autoswipe_enabled_backup), true)
          .apply()
      ImmediatePostAutoSwipeUseCase(this, Schedulers.trampoline()).execute(
          object : DisposableCompletableObserver() {
            override fun onComplete() {
            }

            override fun onError(error: Throwable) {
            }
          })
    }
    HomeActivity.getCallingIntent(this).apply {
      flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
      startActivity(this)
    }
    supportFinishAfterTransition()
  }
}
