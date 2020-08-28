package data.tinder.like

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal class LikeRecommendationActionFactoryModule {
  @Provides
  @Singleton
  fun likeRecommendationActionFactory(): LikeRecommendationActionFactoryWrapper =
      LikeRecommendationActionFactoryWrapper { LikeRecommendationAction(it) }
}
