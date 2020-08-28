package domain.dislike

import domain.interactor.SingleDisposableUseCase
import domain.recommendation.DomainRecommendationUser
import io.reactivex.Scheduler
import io.reactivex.Single

class DislikeRecommendationUseCase(
    private val recommendation: DomainRecommendationUser,
    postExecutionScheduler: Scheduler)
  : SingleDisposableUseCase<DomainDislikedRecommendationAnswer>(
    postExecutionScheduler = postExecutionScheduler) {
  override fun buildUseCase(): Single<DomainDislikedRecommendationAnswer> =
      DislikeRecommendationHolder.dislikeRecommendation.dislikeRecommendation(recommendation)
}
