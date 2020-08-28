package reporter

import android.content.Context

interface CrashReporter {
  fun init(context: Context)

  fun report(throwable: Throwable)
}
