package app

import android.os.Build
import android.os.StrictMode

/**
 * Debug application, for (guess what?!) debugging purposes.
 */
internal class DebugApplication : MainApplication() {
  override fun onCreate() {
    super.onCreate()
    enforceThreadStrictMode()
    enforceVMStrictMode()
  }

  /**
   * Enforces StrictMode for the current thread.
   * @see <a href="https://developer.android.com/reference/android/os/StrictMode.html">
   *     Strict Mode | Android Developers</a>
   */
  private fun enforceThreadStrictMode() =
      StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
          .detectCustomSlowCalls()
          .detectDiskReads()
          .detectDiskWrites()
          .detectNetwork()
          .penaltyDropBox()
          .apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
              detectResourceMismatches()
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                detectUnbufferedIo()
              }
            }
          }
          .build())

  /**
   * Enforces StrictMode for the VM.
   * @see <a href="https://developer.android.com/reference/android/os/StrictMode.html">
   *     Strict Mode | Android Developers</a>
   */
  private fun enforceVMStrictMode() = StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
      .detectActivityLeaks()
      .detectLeakedClosableObjects()
      .detectLeakedRegistrationObjects()
      .detectLeakedSqlLiteObjects()
      .penaltyDropBox()
      .apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          detectCleartextNetwork()
          detectFileUriExposure()
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            detectContentUriWithoutPermission()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
              detectNonSdkApiUsage()
            }
          }
        }
      }
      .build())
}
