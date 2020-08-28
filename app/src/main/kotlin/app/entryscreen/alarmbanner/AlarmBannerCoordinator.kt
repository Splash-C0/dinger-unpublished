package app.entryscreen.alarmbanner

import android.content.Context
import domain.autoswipe.ImmediatePostAutoSwipeUseCase
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.schedulers.Schedulers
import reporter.CrashReporter

internal class AlarmBannerCoordinator(
    private val context: Context,
    private val crashReporter: CrashReporter) {
  private var useCase: ImmediatePostAutoSwipeUseCase? = null

  fun actionDoSchedule() {
    useCase = ImmediatePostAutoSwipeUseCase(context, Schedulers.trampoline())
    useCase?.execute(object : DisposableCompletableObserver() {
      override fun onError(error: Throwable) = crashReporter.report(error)

      override fun onComplete() = Unit
    })
  }

  fun actionCancelSchedule() {
    useCase?.dispose()
  }
}
