package data.tinder.dislike

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal class DislikeRecommendationActionFactoryModule {
  @Provides
  @Singleton
  fun likeRecommendationActionFactory() =
      DislikeRecommendationActionFactoryWrapper { DislikeRecommendationAction(it) }
}
