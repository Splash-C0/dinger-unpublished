package app.entryscreen.login

import android.app.Activity
import android.view.View
import androidx.core.widget.ContentLoadingProgressBar
import app.entryscreen.EntryScreenScope
import app.entryscreen.login.facebook.FacebookLoginCoordinator
import app.entryscreen.login.facebook.FacebookLoginView
import app.entryscreen.login.facebook.TinderFacebookLoginModule
import app.entryscreen.login.sms.SmsLoginView
import app.entryscreen.login.sms.TinderSmsLoginModule
import dagger.BindsInstance
import dagger.Subcomponent

@Subcomponent(modules = [TinderFacebookLoginModule::class, TinderSmsLoginModule::class])
@EntryScreenScope
internal interface TinderLoginComponent {
  fun inject(target: TinderLoginActivity)
  @Subcomponent.Factory
  interface Factory {
    fun create(
        @BindsInstance activity: Activity,
        @BindsInstance @FacebookLoginView facebookLoginView: View,
        @BindsInstance @SmsLoginView smsLoginView: View,
        @BindsInstance contentLoadingProgressBar: ContentLoadingProgressBar,
        @BindsInstance
        facebookLoginResultCallback: FacebookLoginCoordinator.ResultCallback,
        @BindsInstance
        tinderLoginResultCallback: TinderLoginResultCallback): TinderLoginComponent
  }
}
