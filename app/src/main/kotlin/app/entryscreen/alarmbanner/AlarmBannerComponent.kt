package app.entryscreen.alarmbanner

import android.view.View
import app.entryscreen.EntryScreenScope
import dagger.BindsInstance
import dagger.Subcomponent

@Subcomponent(modules = [AlarmBannerModule::class])
@EntryScreenScope
internal interface AlarmBannerComponent {
  fun inject(target: AlarmBannerActivity)
  @Subcomponent.Factory
  interface Factory {
    fun create(
        @BindsInstance continueView: View,
        @BindsInstance continueResultCallback: ContinueCoordinator.ResultCallback)
        : AlarmBannerComponent
  }
}
