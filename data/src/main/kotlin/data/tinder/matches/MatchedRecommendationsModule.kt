package data.tinder.matches

import dagger.Module
import dagger.Provides
import data.tinder.recommendation.RecommendationUserResolver
import data.tinder.recommendation.RecommendationUserResolverModule
import domain.matches.MatchedRecommendations
import javax.inject.Singleton

@Module(includes = [RecommendationUserResolverModule::class])
internal class MatchedRecommendationsModule {
  @Provides
  @Singleton
  fun matchedRecommendations(resolver: RecommendationUserResolver): MatchedRecommendations =
      MatchedRecommendationsImpl(resolver)
}
