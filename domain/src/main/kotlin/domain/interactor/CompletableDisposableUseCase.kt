package domain.interactor

import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.observers.DisposableCompletableObserver

abstract class CompletableDisposableUseCase protected constructor(
    /**
     * Send null for in-place synchronous execution
     */
    private val asyncExecutionScheduler: Scheduler? = null,
    private val postExecutionScheduler: Scheduler)
  : DisposableUseCase(), UseCase<Completable> {
  fun execute(subscriber: DisposableCompletableObserver) {
    assembledSubscriber = buildUseCase().let {
      val completeSetup = { x: Completable ->
        // noinspection CheckResult - False positive
        x.observeOn(postExecutionScheduler).subscribeWith(subscriber)
      }
      if (asyncExecutionScheduler != null) {
        completeSetup(it.subscribeOn(asyncExecutionScheduler))
      } else {
        completeSetup(it)
      }
    }
  }
}
