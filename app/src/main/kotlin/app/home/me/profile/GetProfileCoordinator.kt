package app.home.me.profile

import domain.interactor.DisposableUseCase
import domain.profile.DomainGetProfileAnswer
import domain.profile.GetProfileUseCase
import io.reactivex.Scheduler
import io.reactivex.observers.DisposableSingleObserver
import reporter.CrashReporter

internal class GetProfileCoordinator(
    private val view: ProfileView,
    private val asyncExecutionScheduler: Scheduler,
    private val postExecutionScheduler: Scheduler,
    private val crashReporter: CrashReporter) {
  private var useCase: DisposableUseCase? = null

  fun actionGet() {
    useCase?.dispose()
    view.setProgress()
    GetProfileUseCase(asyncExecutionScheduler, postExecutionScheduler).apply {
      useCase = this
      execute(object : DisposableSingleObserver<DomainGetProfileAnswer>() {
        override fun onError(error: Throwable) {
          crashReporter.report(error)
          view.setError()
        }

        override fun onSuccess(t: DomainGetProfileAnswer) {
          view.setData(t)
        }
      })
    }
  }

  fun actionCancel() {
    view.setError()
    useCase?.dispose()
  }
}
