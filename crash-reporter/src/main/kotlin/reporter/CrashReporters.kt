package reporter

abstract class CrashReporters {
  companion object {
    fun void(): CrashReporter = CrashReporterImpl.Void
    fun bugsnag(): CrashReporter = CrashReporterImpl.Bugsnag
  }
}
