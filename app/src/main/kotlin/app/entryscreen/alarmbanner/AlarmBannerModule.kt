package app.entryscreen.alarmbanner

import android.content.Context
import android.view.View
import app.entryscreen.EntryScreenScope
import dagger.Module
import dagger.Provides
import reporter.CrashReporter

@Module
internal class AlarmBannerModule {
  @Provides
  @EntryScreenScope
  fun view(view: View): ContinueView = ContinueViewImpl(view)

  @Provides
  @EntryScreenScope
  fun continueCoordinator(callback: ContinueCoordinator.ResultCallback, view: ContinueView) =
      ContinueCoordinator(callback, view)

  @Provides
  @EntryScreenScope
  fun alarmBannerCoordinator(
      context: Context,
      crashReporter: CrashReporter) = AlarmBannerCoordinator(context, crashReporter)
}
