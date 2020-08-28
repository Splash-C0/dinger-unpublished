package reporter

import android.content.Context
import android.util.Log

internal sealed class CrashReporterImpl : CrashReporter {
  object Void : CrashReporterImpl() {
    override fun init(context: Context) {
      Log.i("Void-debug-logs", "init")
    }

    override fun report(throwable: Throwable) {
      Log.i("Void-debug-logs", throwable.message, throwable)
    }
  }

  object Bugsnag : CrashReporterImpl() {
    override fun init(context: Context) {
      Log.i("Bugsnag-debug-logs", "init")
    }

    override fun report(throwable: Throwable) {
      Log.i("Bugsnag-debug-logs", throwable.message, throwable)
    }
  }
}
