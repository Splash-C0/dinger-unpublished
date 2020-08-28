package app.crash

import dagger.Module
import dagger.Provides
import reporter.CrashReporter
import reporter.CrashReporters

@Module
internal class CrashReporterModule {
  @Provides
  fun bugsnag(): CrashReporter = CrashReporters.bugsnag()
}
