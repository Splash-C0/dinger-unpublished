package app.entryscreen.splash

import android.app.Activity
import app.entryscreen.EntryScreenScope
import dagger.BindsInstance
import dagger.Subcomponent

@Subcomponent(modules = [SplashModule::class])
@EntryScreenScope
internal interface SplashComponent {
  fun inject(target: SplashActivity)
  @Subcomponent.Factory
  interface Factory {
    fun create(
        @BindsInstance activity: Activity,
        @BindsInstance loggedInCheckResultCallback: LoggedInCheckCoordinator.ResultCallback)
        : SplashComponent
  }
}
