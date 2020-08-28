package data

import dagger.Component
import data.account.AccountModule
import data.alarm.AlarmModule
import data.autoswipe.AutoSwipeModule
import data.autoswipe.AutoSwipeServiceDestructorModule
import data.crash.CrashReporterModule
import data.network.NetworkModule
import data.seen.SeenRecommendationsModule
import data.storage.StorageClearModule
import data.tinder.dislike.DislikeRecommendationModule
import data.tinder.like.LikeRecommendationModule
import data.tinder.login.facebook.FacebookLoginModule
import data.tinder.login.sms.SmsLoginModule
import data.tinder.matches.MatchedRecommendationsModule
import data.tinder.profile.GetProfileModule
import data.tinder.recommendation.GetRecommendationModule
import javax.inject.Singleton

@Component(modules = [
  CrashReporterModule::class,
  NetworkModule::class,
  FacebookLoginModule::class,
  SmsLoginModule::class,
  GetRecommendationModule::class,
  LikeRecommendationModule::class,
  DislikeRecommendationModule::class,
  AccountModule::class,
  AlarmModule::class,
  AutoSwipeModule::class,
  StorageClearModule::class,
  AutoSwipeServiceDestructorModule::class,
  SeenRecommendationsModule::class,
  GetProfileModule::class,
  MatchedRecommendationsModule::class])
@Singleton
internal interface InitializationComponent {
  fun inject(initializationContentProvider: InitializationContentProvider)
}
