package data.tinder.recommendation

import domain.recommendation.DomainRecommendationUser
import domain.recommendation.GetRecommendation
import io.reactivex.Single
import reporter.CrashReporter

internal class GetRecommendationImpl(
    private val getRecommendationFacade: GetRecommendationFacade,
    private val crashReporter: CrashReporter) : GetRecommendation {
  override fun getRecommendations(): Single<List<DomainRecommendationUser>> =
      getRecommendationFacade.fetch(Unit).doOnError { crashReporter.report(it) }
}
