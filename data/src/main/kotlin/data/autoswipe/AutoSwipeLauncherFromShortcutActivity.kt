package data.autoswipe

import android.app.Activity
import android.os.Bundle
import domain.autoswipe.ImmediatePostAutoSwipeUseCase
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.schedulers.Schedulers
import reporter.CrashReporters

internal class AutoSwipeLauncherFromShortcutActivity : Activity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    ImmediatePostAutoSwipeUseCase(this, Schedulers.trampoline()).execute(
        object : DisposableCompletableObserver() {
          override fun onComplete() {
            finish()
          }

          override fun onError(e: Throwable) {
            CrashReporters.bugsnag().report(e)
            finish()
          }
        })
  }
}
