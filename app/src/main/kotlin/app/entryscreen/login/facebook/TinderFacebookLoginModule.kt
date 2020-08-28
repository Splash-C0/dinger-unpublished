package app.entryscreen.login.facebook

import android.app.Activity
import android.view.View
import androidx.core.widget.ContentLoadingProgressBar
import app.entryscreen.EntryScreenScope
import app.entryscreen.login.TinderLoginResultCallback
import app.entryscreen.login.TinderLoginView
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import reporter.CrashReporter
import javax.inject.Named
import javax.inject.Qualifier

@Module
internal class TinderFacebookLoginModule {
  @Provides
  @EntryScreenScope
  fun facebookLoginCoordinator(
      @FacebookLoginView facebookLoginView: View,
      facebookLoginResultCallback: FacebookLoginCoordinator.ResultCallback,
      crashReporter: CrashReporter) =
      FacebookLoginCoordinator(
          facebookLoginView,
          facebookLoginResultCallback,
          crashReporter)

  @Provides
  @EntryScreenScope
  @TinderFacebookLoginLocal
  fun view(
      activity: Activity,
      @FacebookLoginView facebookLoginView: View,
      contentLoadingProgressBar: ContentLoadingProgressBar): TinderLoginView =
      TinderFacebookLoginView(
          activity, facebookLoginView, contentLoadingProgressBar)

  @Provides
  @EntryScreenScope
  fun tinderLoginCoordinator(
      @TinderFacebookLoginLocal view: TinderLoginView,
      @Named("io") asyncExecutionScheduler: Scheduler,
      @Named("main") postExecutionScheduler: Scheduler,
      tinderLoginResultCallback: TinderLoginResultCallback,
      crashReporter: CrashReporter) =
      TinderFacebookLoginCoordinator(
          view,
          asyncExecutionScheduler,
          postExecutionScheduler,
          tinderLoginResultCallback,
          crashReporter)
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class FacebookLoginView

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
private annotation class TinderFacebookLoginLocal
