package data.crash

import dagger.Module
import dagger.Provides
import reporter.CrashReporters
import javax.inject.Singleton

@Module
internal class CrashReporterModule {
  @Provides
  @Singleton
  fun instance() = CrashReporters.bugsnag()
}
