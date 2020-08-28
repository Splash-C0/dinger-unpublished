package app.home.matches

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import app.home.HomeScreenScope
import dagger.BindsInstance
import dagger.Component

@Component(modules = [MatchesModule::class])
@HomeScreenScope
internal interface MatchesComponent {
  fun inject(target: MatchesFragment)
  @Component.Factory
  interface Factory {
    fun create(
        @BindsInstance context: Context,
        @BindsInstance fragment: Fragment,
        @BindsInstance emptyView: View): MatchesComponent
  }
}
