package data.tinder.login.sms

import dagger.Module
import dagger.Provides
import data.crash.CrashReporterModule
import reporter.CrashReporter
import javax.inject.Singleton

@Module(includes = [CrashReporterModule::class, SmsRequestOneTimePasswordSourceModule::class])
internal class SmsRequestOneTimePasswordFacadeModule {
  @Provides
  @Singleton
  fun requestObjectMapper(crashReporter: CrashReporter) =
      SmsRequestOneTimePasswordRequestObjectMapper(crashReporter)

  @Provides
  @Singleton
  fun responseObjectMapper(crashReporter: CrashReporter) =
      SmsRequestOneTimePasswordResponseObjectMapper(crashReporter)

  @Provides
  @Singleton
  fun facade(
      source: SmsRequestOneTimePasswordSource,
      requestObjectMapper: SmsRequestOneTimePasswordRequestObjectMapper,
      responseObjectMapper: SmsRequestOneTimePasswordResponseObjectMapper) =
      SmsRequestOneTimePasswordFacade(source, requestObjectMapper, responseObjectMapper)
}
