package domain.profile

import domain.interactor.SingleDisposableUseCase
import io.reactivex.Scheduler
import io.reactivex.Single

class GetProfileUseCase(executionScheduler: Scheduler, postExecutionScheduler: Scheduler)
  : SingleDisposableUseCase<DomainGetProfileAnswer>(
    executionScheduler = executionScheduler,
    postExecutionScheduler = postExecutionScheduler) {
  override fun buildUseCase(): Single<DomainGetProfileAnswer> =
      GetProfileHolder.getProfile.getProfile()
}
