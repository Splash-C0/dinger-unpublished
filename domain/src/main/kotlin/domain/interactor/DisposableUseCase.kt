package domain.interactor

import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables

abstract class DisposableUseCase protected constructor() {
  protected var assembledSubscriber: Disposable = Disposables.empty()

  /**
   * Tears down the use case if required.
   */
  fun dispose() = assembledSubscriber.dispose()
}
