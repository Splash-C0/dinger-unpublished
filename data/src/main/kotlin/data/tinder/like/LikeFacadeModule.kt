package data.tinder.like

import android.content.Context
import dagger.Module
import dagger.Provides
import data.crash.CrashReporterModule
import reporter.CrashReporter
import javax.inject.Singleton

@Module(includes = [
  CrashReporterModule::class,
  LikeSourceModule::class,
  LikeEventTrackerModule::class])
internal class LikeFacadeModule {
  @Provides
  @Singleton
  fun requestObjectMapper(crashReporter: CrashReporter, context: Context) =
      LikeRequestObjectMapper(crashReporter, context)

  @Provides
  @Singleton
  fun responseObjectMapper(crashReporter: CrashReporter, eventTracker: LikeEventTracker) =
      LikeResponseObjectMapper(crashReporter, eventTracker)

  @Provides
  @Singleton
  fun facade(
      source: LikeSource,
      requestObjectMapper: LikeRequestObjectMapper,
      responseObjectMapper: LikeResponseObjectMapper) =
      LikeFacade(source, requestObjectMapper, responseObjectMapper)
}
