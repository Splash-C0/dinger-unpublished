package app.entryscreen.login.facebook

import app.entryscreen.login.TinderLoginResultCallback
import app.entryscreen.login.TinderLoginView
import domain.interactor.CompletableDisposableUseCase
import domain.login.facebook.TinderFacebookLoginUseCase
import io.reactivex.Scheduler
import io.reactivex.observers.DisposableCompletableObserver
import reporter.CrashReporter

internal class TinderFacebookLoginCoordinator(
    private val view: TinderLoginView,
    private val asyncExecutionScheduler: Scheduler,
    private val postExecutionScheduler: Scheduler,
    private val tinderLoginResultCallback: TinderLoginResultCallback,
    private val crashReporter: CrashReporter) {
  private var useCase: CompletableDisposableUseCase? = null

  fun actionDoLogin(facebookId: String, facebookToken: String) {
    useCase?.dispose()
    view.setRunning()
    TinderFacebookLoginUseCase(
        facebookId, facebookToken, asyncExecutionScheduler, postExecutionScheduler).apply {
      useCase = this
      execute(object : DisposableCompletableObserver() {
        override fun onError(error: Throwable) {
          crashReporter.report(error)
          view.setError(error)
        }

        override fun onComplete() {
          tinderLoginResultCallback.onTinderLoginSuccess()
          view.setStale()
        }
      })
    }
  }

  fun actionCancel() {
    view.setStale()
    useCase?.dispose()
  }
}
