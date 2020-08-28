package data.tinder.login.sms

import dagger.Module
import dagger.Provides
import data.crash.CrashReporterModule
import reporter.CrashReporter
import javax.inject.Singleton

@Module(includes = [CrashReporterModule::class, SmsVerifyOneTimePasswordSourceModule::class])
internal class SmsVerifyOneTimePasswordFacadeModule {
  @Provides
  @Singleton
  fun requestObjectMapper(crashReporter: CrashReporter) =
      SmsVerifyOneTimePasswordRequestObjectMapper(crashReporter)

  @Provides
  @Singleton
  fun responseObjectMapper(crashReporter: CrashReporter) =
      SmsVerifyOneTimePasswordResponseObjectMapper(crashReporter)

  @Provides
  @Singleton
  fun facade(
      source: SmsVerifyOneTimePasswordSource,
      requestObjectMapper: SmsVerifyOneTimePasswordRequestObjectMapper,
      responseObjectMapper: SmsVerifyOneTimePasswordResponseObjectMapper) =
      SmsVerifyOneTimePasswordFacade(source, requestObjectMapper, responseObjectMapper)
}
