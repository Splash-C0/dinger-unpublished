package data.autoswipe

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import data.RootModule
import data.preferences.DefaultSharedPreferencesModule
import data.tinder.dislike.DislikeRecommendationActionFactoryModule
import data.tinder.dislike.DislikeRecommendationActionFactoryWrapper
import data.tinder.like.LikeRecommendationActionFactoryModule
import data.tinder.like.LikeRecommendationActionFactoryWrapper
import javax.inject.Singleton

@Module(includes = [
  DefaultSharedPreferencesModule::class,
  DislikeRecommendationActionFactoryModule::class,
  LikeRecommendationActionFactoryModule::class,
  RootModule::class])
internal class ProcessRecommendationActionFactoryModule {
  @Provides
  @Singleton
  fun processRecommendationActionFactory(
      rootContext: Context,
      defaultSharedPreferences: SharedPreferences,
      likeRecommendationActionFactory: dagger.Lazy<LikeRecommendationActionFactoryWrapper>,
      dislikeRecommendationActionFactory
      : dagger.Lazy<DislikeRecommendationActionFactoryWrapper>) =
      ProcessRecommendationActionFactoryWrapper {
        ProcessRecommendationAction(
            rootContext,
            it,
            defaultSharedPreferences,
            likeRecommendationActionFactory,
            dislikeRecommendationActionFactory)
      }
}
