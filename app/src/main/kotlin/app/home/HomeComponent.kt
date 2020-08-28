package app.home

import android.app.Activity
import android.content.Context
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = [HomeModule::class])
@Singleton
internal interface HomeComponent {
  fun inject(target: HomeActivity)
  @Component.Factory
  interface Factory {
    fun create(
        @BindsInstance activity: Activity,
        @BindsInstance fragmentManager: FragmentManager,
        @BindsInstance bottomNavigationView: BottomNavigationView,
        @BindsInstance viewPager: ViewPager,
        @BindsInstance context: Context,
        @BindsInstance searchView: SearchView): HomeComponent
  }
}
