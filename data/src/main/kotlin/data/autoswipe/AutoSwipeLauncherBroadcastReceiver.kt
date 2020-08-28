package data.autoswipe

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.AndroidRuntimeException
import reporter.CrashReporters

internal class AutoSwipeLauncherBroadcastReceiver : BroadcastReceiver() {
  private val crashReporter by lazy { CrashReporters.bugsnag() }

  @SuppressLint("UnsafeProtectedBroadcastReceiver")
  override fun onReceive(context: Context?, intent: Intent?) {
    context?.apply {
      if (intent!!.getBooleanExtra(EXTRA_KEY_CLOSE_BATCH, false)) {
        LikeBatchTracker.apply {
          init(applicationContext)
          closeBatch()
        }
      }
      AutoSwipeIntentService.callingIntent(context).let {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          try {
            startForegroundService(it)
          } catch (didNotCallStartForegroundFastEnough: AndroidRuntimeException) {
            // RemoteServiceException
            crashReporter.report(didNotCallStartForegroundFastEnough)
            onReceive(context, intent)
          }
        } else {
          startService(it)
        }
      }
    }
  }

  companion object {
    fun getCallingIntent(context: Context) =
        Intent(context, AutoSwipeLauncherBroadcastReceiver::class.java)
  }
}

internal const val EXTRA_KEY_CLOSE_BATCH = "EXTRA_KEY_CLOSE_BATCH"
