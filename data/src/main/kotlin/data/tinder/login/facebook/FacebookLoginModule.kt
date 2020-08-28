package data.tinder.login.facebook

import dagger.Module
import dagger.Provides
import data.crash.CrashReporterModule
import domain.login.facebook.FacebookLogin
import reporter.CrashReporter
import javax.inject.Singleton

@Module(includes = [FacebookLoginFacadeModule::class, CrashReporterModule::class])
internal class FacebookLoginModule {
  @Provides
  @Singleton
  fun login(loginFacade: FacebookLoginFacade, crashReporter: CrashReporter): FacebookLogin =
      FacebookLoginImpl(loginFacade, crashReporter)
}
