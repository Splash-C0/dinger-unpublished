package data.tinder.dislike

import dagger.Module
import dagger.Provides
import data.crash.CrashReporterModule
import reporter.CrashReporter
import javax.inject.Singleton

@Module(includes = [CrashReporterModule::class, DislikeSourceModule::class])
internal class DislikeFacadeModule {
  @Provides
  @Singleton
  fun requestObjectMapper(crashReporter: CrashReporter) = DislikeRequestObjectMapper(crashReporter)

  @Provides
  @Singleton
  fun responseObjectMapper(crashReporter: CrashReporter) =
      DislikeResponseObjectMapper(crashReporter)

  @Provides
  @Singleton
  fun facade(
      source: DislikeSource,
      requestObjectMapper: DislikeRequestObjectMapper,
      responseObjectMapper: DislikeResponseObjectMapper) =
      DislikeFacade(source, requestObjectMapper, responseObjectMapper)
}
