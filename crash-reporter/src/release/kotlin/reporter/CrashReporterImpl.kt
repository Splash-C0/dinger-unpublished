package reporter

import android.content.Context

internal sealed class CrashReporterImpl : CrashReporter {
  object Void : CrashReporterImpl() {
    override fun init(context: Context) = Unit

    override fun report(throwable: Throwable) = Unit
  }

  object Bugsnag : CrashReporterImpl() {
    override fun init(context: Context) {
      com.bugsnag.android.Bugsnag.start(context)
    }

    override fun report(throwable: Throwable) {
      com.bugsnag.android.Bugsnag.notify(throwable)
    }
  }
}
