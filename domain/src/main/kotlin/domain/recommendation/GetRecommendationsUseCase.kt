package domain.recommendation

import domain.interactor.SingleDisposableUseCase
import io.reactivex.Scheduler

class GetRecommendationsUseCase(
    postExecutionScheduler: Scheduler)
  : SingleDisposableUseCase<List<DomainRecommendationUser>>(
    postExecutionScheduler = postExecutionScheduler) {
  override fun buildUseCase() =
      GetRecommendationHolder.getRecommendation.getRecommendations()
}
