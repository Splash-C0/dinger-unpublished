package app.entryscreen.login.sms

import android.content.Context
import androidx.core.widget.ContentLoadingProgressBar
import app.entryscreen.EntryScreenScope
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import otpview.OtpView
import reporter.CrashReporter
import javax.inject.Named
import javax.inject.Qualifier

@Module
internal class TinderSmsLoginVerifyOneTimePasswordModule {
  @Provides
  @EntryScreenScope
  @TinderSmsLoginVerifyOneTimePasswordModuleLocal
  fun view(
      otpView: OtpView,
      contentLoadingProgressBar: ContentLoadingProgressBar): TinderSmsVerifyOneTimePasswordView =
      TinderSmsVerifyOneTimePasswordViewImpl(otpView, contentLoadingProgressBar)

  @Provides
  @EntryScreenScope
  fun verifyOtpCoordinator(
      context: Context,
      @Named("io") asyncExecutionScheduler: Scheduler,
      @Named("main") postExecutionScheduler: Scheduler,
      @TinderSmsLoginVerifyOneTimePasswordModuleLocal
      tinderSmsVerifyOneTimePasswordView: TinderSmsVerifyOneTimePasswordView,
      verifyOneTimePasswordCoordinatorResultCallback: TinderSmsVerifyOneTimePasswordCoordinator.ResultCallback,
      crashReporter: CrashReporter) =
      TinderSmsVerifyOneTimePasswordCoordinator(
          context,
          asyncExecutionScheduler,
          postExecutionScheduler,
          tinderSmsVerifyOneTimePasswordView,
          verifyOneTimePasswordCoordinatorResultCallback,
          crashReporter)
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
private annotation class TinderSmsLoginVerifyOneTimePasswordModuleLocal
