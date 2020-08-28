package domain.loggedincheck

import domain.interactor.SingleDisposableUseCase
import io.reactivex.Scheduler
import io.reactivex.Single

class LoggedInUserCheckUseCase(
    asyncExecutionScheduler: Scheduler,
    postExecutionScheduler: Scheduler)
  : SingleDisposableUseCase<Boolean>(asyncExecutionScheduler, postExecutionScheduler) {
  override fun buildUseCase(): Single<Boolean> =
      Single.just(LoggedInCheckHolder.loggedInCheck.isThereALoggedInUser())
}
