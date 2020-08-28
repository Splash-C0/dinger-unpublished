package app.entryscreen.login.sms

import android.content.Context
import domain.interactor.CompletableDisposableUseCase
import domain.login.sms.TinderSmsVerifyOneTimePasswordUseCase
import io.reactivex.Scheduler
import io.reactivex.observers.DisposableCompletableObserver
import reporter.CrashReporter

internal class TinderSmsVerifyOneTimePasswordCoordinator(
    private val context: Context,
    private val asyncExecutionScheduler: Scheduler,
    private val postExecutionScheduler: Scheduler,
    private val verifyOtpView: TinderSmsVerifyOneTimePasswordView,
    private val resultCallback: ResultCallback,
    private val crashReporter: CrashReporter) {
  private var useCase: CompletableDisposableUseCase? = null

  fun bind(phoneNumber: String, otpLength: Int) {
    verifyOtpView.apply {
      setOtpLength(otpLength)
      verifyOtpView.setOtpViewListener {
        setRunning()
        useCase = TinderSmsVerifyOneTimePasswordUseCase(
            phoneNumber = phoneNumber,
            otp = it,
            asyncExecutionScheduler = asyncExecutionScheduler,
            postExecutionScheduler = postExecutionScheduler).apply {
          execute(object : DisposableCompletableObserver() {
            override fun onComplete() {
              setStale()
              resultCallback.onSmsOneTimePasswordVerified()
            }

            override fun onError(e: Throwable)  {
              crashReporter.report(e)
              setError(e)
            }
          })
        }
      }
    }
  }

  fun release() {
    useCase?.dispose()?.also {
      useCase = null
    }
  }

  internal interface ResultCallback {
    fun onSmsOneTimePasswordVerified()
  }
}
