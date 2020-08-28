package app.home.seen

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import app.home.HomeScreenScope
import dagger.BindsInstance
import dagger.Component

@Component(modules = [SeenModule::class])
@HomeScreenScope
internal interface SeenComponent {
  fun inject(target: SeenFragment)
  @Component.Factory
  interface Factory {
    fun create(
        @BindsInstance context: Context,
        @BindsInstance fragment: Fragment,
        @BindsInstance emptyView: View): SeenComponent
  }
}
