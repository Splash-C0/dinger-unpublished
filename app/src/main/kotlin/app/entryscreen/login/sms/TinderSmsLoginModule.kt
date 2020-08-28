package app.entryscreen.login.sms

import android.app.Activity
import android.view.View
import app.entryscreen.EntryScreenScope
import dagger.Module
import dagger.Provides
import javax.inject.Qualifier

@Module
internal class TinderSmsLoginModule {
  @Provides
  @EntryScreenScope
  fun coordinator(
      activity: Activity,
      @SmsLoginView triggerView: View) = TinderSmsLoginCoordinator(activity, triggerView)
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class SmsLoginView
