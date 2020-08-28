package data.tinder.dislike

import domain.dislike.DislikeRecommendation
import domain.dislike.DomainDislikedRecommendationAnswer
import domain.recommendation.DomainRecommendationUser
import io.reactivex.Single
import reporter.CrashReporter

internal class DislikeRecommendationImpl(
    private val dislikeFacade: DislikeFacade,
    private val crashReporter: CrashReporter) : DislikeRecommendation {
  override fun dislikeRecommendation(recommendation: DomainRecommendationUser)
      : Single<DomainDislikedRecommendationAnswer> =
      dislikeFacade.fetch(recommendation).doOnError { crashReporter.report(it) }
}
