package data.tinder.login.facebook

import dagger.Module
import dagger.Provides
import data.crash.CrashReporterModule
import reporter.CrashReporter
import javax.inject.Singleton

@Module(includes = [CrashReporterModule::class, FacebookLoginSourceModule::class])
internal class FacebookLoginFacadeModule {
  @Provides
  @Singleton
  fun requestObjectMapper(crashReporter: CrashReporter) =
      FacebookLoginRequestObjectMapper(crashReporter)

  @Provides
  @Singleton
  fun responseObjectMapper(crashReporter: CrashReporter) =
      FacebookLoginResponseObjectMapper(crashReporter)

  @Provides
  @Singleton
  fun facade(
      source: FacebookLoginSource,
      requestObjectMapper: FacebookLoginRequestObjectMapper,
      responseObjectMapper: FacebookLoginResponseObjectMapper) =
      FacebookLoginFacade(source, requestObjectMapper, responseObjectMapper)
}
