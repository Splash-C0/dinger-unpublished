package app.home.me.logout

import android.content.Context
import domain.logout.LogoutHolder
import domain.logout.LogoutUseCase
import io.reactivex.observers.DisposableCompletableObserver

internal class LogoutCoordinator(private val context: Context) {
  private var useCase: LogoutUseCase? = null

  fun actionRun() {
    useCase?.dispose()
    LogoutUseCase(context).apply {
      useCase = this
      execute(object : DisposableCompletableObserver() {
        override fun onComplete() {
          LogoutHolder.storageClear.clearStorage(context)
        }

        override fun onError(e: Throwable) {
          throw IllegalStateException("Logging out should always succeed, but failed", e)
        }
      })
    }
  }

  fun actionCancel() {
    useCase?.dispose()
  }
}
