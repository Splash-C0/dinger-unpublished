package data.autoswipe

import dagger.Component
import data.account.AccountModule
import data.crash.CrashReporterModule
import data.preferences.DefaultSharedPreferencesModule
import data.tinder.recommendation.GetRecommendationsActionModule
import data.tinder.recommendation.RecommendationUserResolverModule
import javax.inject.Singleton

@Component(modules = [
  AccountModule::class,
  AutoSwipeReportHandlerModule::class,
  DefaultSharedPreferencesModule::class,
  CrashReporterModule::class,
  GetRecommendationsActionModule::class,
  ProcessRecommendationActionFactoryModule::class,
  RecommendationUserResolverModule::class])
@Singleton
internal interface AutoSwipeComponent {
  fun inject(autoSwipeIntentService: AutoSwipeIntentService)
}
