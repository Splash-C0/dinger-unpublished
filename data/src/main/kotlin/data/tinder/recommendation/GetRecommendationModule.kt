package data.tinder.recommendation

import dagger.Module
import dagger.Provides
import data.crash.CrashReporterModule
import domain.recommendation.GetRecommendation
import reporter.CrashReporter
import javax.inject.Singleton

@Module(includes = [GetRecommendationFacadeModule::class, CrashReporterModule::class])
internal class GetRecommendationModule {
  @Provides
  @Singleton
  fun getRecommendation(getRecommendationFacade: GetRecommendationFacade,
                        crashReporter: CrashReporter): GetRecommendation =
      GetRecommendationImpl(getRecommendationFacade, crashReporter)
}
