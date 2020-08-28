package data.seen

import dagger.Module
import dagger.Provides
import data.tinder.recommendation.RecommendationUserResolver
import data.tinder.recommendation.RecommendationUserResolverModule
import domain.seen.SeenRecommendation
import domain.seen.SeenRecommendations
import javax.inject.Singleton

@Module(includes = [RecommendationUserResolverModule::class])
internal class SeenRecommendationsModule {
  @Provides
  @Singleton
  fun seenRecommendations(resolver: RecommendationUserResolver): SeenRecommendations =
      SeenRecommendationsImpl(resolver)

  @Provides
  @Singleton
  fun seenRecommendation(resolver: RecommendationUserResolver): SeenRecommendation =
      SeenRecommendationImpl(resolver)
}
