package app.entryscreen.splash

import app.entryscreen.EntryScreenScope
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import reporter.CrashReporter
import javax.inject.Named

@Module
internal class SplashModule {
  @Provides
  @EntryScreenScope
  fun loggedInCheckCoordinator(
      @Named("io") asyncExecutionScheduler: Scheduler,
      @Named("main") postExecutionScheduler: Scheduler,
      crashReporter: CrashReporter,
      loggedInCheckResultCallback: LoggedInCheckCoordinator.ResultCallback) = LoggedInCheckCoordinator(
      asyncExecutionScheduler,
      postExecutionScheduler,
      loggedInCheckResultCallback,
      crashReporter)
}
