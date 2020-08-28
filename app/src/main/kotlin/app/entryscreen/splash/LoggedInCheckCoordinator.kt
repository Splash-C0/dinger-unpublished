package app.entryscreen.splash

import domain.loggedincheck.LoggedInUserCheckUseCase
import io.reactivex.Scheduler
import io.reactivex.observers.DisposableSingleObserver
import reporter.CrashReporter

internal class LoggedInCheckCoordinator(
    private val asyncExecutionScheduler: Scheduler,
    private val postExecutionScheduler: Scheduler,
    private val resultCallback: ResultCallback,
    private val crashReporter: CrashReporter) {
  private var useCase: LoggedInUserCheckUseCase? = null

  fun actionRun() {
    useCase = LoggedInUserCheckUseCase(asyncExecutionScheduler, postExecutionScheduler)
    useCase?.execute(object : DisposableSingleObserver<Boolean>() {
      override fun onSuccess(foundAUser: Boolean) = when (foundAUser) {
        true -> resultCallback.onLoggedInUserFound()
        false -> resultCallback.onLoggedInUserNotFound()
      }

      override fun onError(error: Throwable) {
        crashReporter.report(error)
        resultCallback.onLoggedInUserNotFound()
      }
    })
  }

  fun actionCancel() {
    useCase?.dispose()
  }

  interface ResultCallback {
    fun onLoggedInUserFound()

    fun onLoggedInUserNotFound()
  }
}
