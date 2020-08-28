package data.tinder.login.sms

import dagger.Module
import dagger.Provides
import data.crash.CrashReporterModule
import reporter.CrashReporter
import javax.inject.Singleton

@Module(includes = [CrashReporterModule::class, SmsLoginSourceModule::class])
internal class SmsLoginFacadeModule {
  @Provides
  @Singleton
  fun requestObjectMapper(crashReporter: CrashReporter) =
      SmsLoginRequestObjectMapper(crashReporter)

  @Provides
  @Singleton
  fun responseObjectMapper(crashReporter: CrashReporter) =
      SmsLoginResponseObjectMapper(crashReporter)

  @Provides
  @Singleton
  fun facade(
      source: SmsLoginSource,
      requestObjectMapper: SmsLoginRequestObjectMapper,
      responseObjectMapper: SmsLoginResponseObjectMapper) =
      SmsLoginFacade(source, requestObjectMapper, responseObjectMapper)
}
