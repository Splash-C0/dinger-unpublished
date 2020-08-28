package app

import app.crash.CrashReporterModule
import dagger.Component
import javax.inject.Singleton

@Component(modules = [CrashReporterModule::class])
@Singleton
internal interface MainApplicationComponent {
  fun inject(mainApplication: MainApplication)
}
