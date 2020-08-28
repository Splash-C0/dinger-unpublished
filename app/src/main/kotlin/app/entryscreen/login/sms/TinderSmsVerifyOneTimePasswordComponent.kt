package app.entryscreen.login.sms

import androidx.core.widget.ContentLoadingProgressBar
import app.entryscreen.EntryScreenScope
import dagger.BindsInstance
import dagger.Subcomponent
import otpview.OtpView

@Subcomponent(modules = [TinderSmsLoginVerifyOneTimePasswordModule::class])
@EntryScreenScope
internal interface TinderSmsVerifyOneTimePasswordComponent {
  fun inject(target: TinderSmsVerifyOneTimePasswordActivity)

  @Subcomponent.Factory
  interface Factory {
    fun create(
        @BindsInstance otpView: OtpView,
        @BindsInstance contentLoadingProgressBar: ContentLoadingProgressBar,
        @BindsInstance
        tinderSmsVerifyOneTimePasswordCoordinatorResultCallback: TinderSmsVerifyOneTimePasswordCoordinator.ResultCallback)
        : TinderSmsVerifyOneTimePasswordComponent
  }
}
