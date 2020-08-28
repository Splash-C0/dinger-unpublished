package domain.like

import domain.interactor.SingleDisposableUseCase
import domain.recommendation.DomainRecommendationUser
import io.reactivex.Scheduler
import io.reactivex.Single

class LikeRecommendationUseCase(
    private val recommendation: DomainRecommendationUser,
    postExecutionScheduler: Scheduler)
  : SingleDisposableUseCase<DomainLikedRecommendationAnswer>(
    postExecutionScheduler = postExecutionScheduler) {
  override fun buildUseCase(): Single<DomainLikedRecommendationAnswer> =
      LikeRecommendationHolder.likeRecommendation.likeRecommendation(recommendation)
}
