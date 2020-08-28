package app.home.me

import android.view.View
import app.common.di.SchedulersModule
import app.home.HomeScreenScope
import dagger.BindsInstance
import dagger.Component

@Component(modules = [MeModule::class, SchedulersModule::class])
@HomeScreenScope
internal interface MeComponent {
  fun inject(target: MeFragment)
  @Component.Factory
  interface Factory {
    fun create(@BindsInstance view: View): MeComponent
  }
}
