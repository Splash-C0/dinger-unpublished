package data.tinder.like

import domain.like.DomainLikedRecommendationAnswer
import domain.like.LikeRecommendation
import domain.recommendation.DomainRecommendationUser
import io.reactivex.Single
import reporter.CrashReporter

internal class LikeRecommendationImpl(
    private val likeFacade: LikeFacade,
    private val crashReporter: CrashReporter) : LikeRecommendation {
  override fun likeRecommendation(recommendation: DomainRecommendationUser)
      : Single<DomainLikedRecommendationAnswer> =
      likeFacade.fetch(recommendation).doOnError { crashReporter.report(it) }
}
