package data.tinder.profile

import dagger.Module
import dagger.Provides
import data.crash.CrashReporterModule
import domain.profile.GetProfile
import reporter.CrashReporter
import javax.inject.Singleton

@Module(includes = [GetProfileFacadeModule::class, CrashReporterModule::class])
internal class GetProfileModule {
  @Provides
  @Singleton
  fun getProfile(getProfileFacade: GetProfileFacade, crashReporter: CrashReporter): GetProfile =
      GetProfileImpl(getProfileFacade, crashReporter)
}
