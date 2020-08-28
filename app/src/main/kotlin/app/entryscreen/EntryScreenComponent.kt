package app.entryscreen

import android.content.Context
import app.common.di.SchedulersModule
import app.crash.CrashReporterModule
import app.entryscreen.alarmbanner.AlarmBannerComponent
import app.entryscreen.login.TinderLoginComponent
import app.entryscreen.login.sms.TinderSmsVerifyOneTimePasswordComponent
import app.entryscreen.splash.SplashComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = [CrashReporterModule::class, SchedulersModule::class])
@Singleton
internal interface EntryScreenComponent {
  fun newSplashComponentFactory(): SplashComponent.Factory
  fun newTinderLoginComponentFactory(): TinderLoginComponent.Factory
  fun newAlarmBannerComponentFactory(): AlarmBannerComponent.Factory
  fun newTinderSmsLoginVerifyOneTimePasswordComponentFactory(): TinderSmsVerifyOneTimePasswordComponent.Factory
  @Component.Factory
  interface Factory {
    fun create(@BindsInstance context: Context): EntryScreenComponent
  }
}
