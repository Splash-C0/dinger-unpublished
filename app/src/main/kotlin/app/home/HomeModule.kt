package app.home

import android.app.Activity
import android.content.Context
import android.widget.Filterable
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.preference.PreferenceManager
import androidx.viewpager.widget.ViewPager
import app.home.rate.RateFeature
import app.home.seen.filter.FilterDelegate
import app.settings.SettingsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal class HomeModule {
  @Provides
  @Singleton
  fun pagerAdapter(fragmentManager: FragmentManager): FragmentStatePagerAdapter =
      HomeFragmentPagerAdapter(fragmentManager)

  @Provides
  @Singleton
  fun onHomePageChangeListener(bottomNavigationView: BottomNavigationView)
      : ViewPager.OnPageChangeListener = OnHomePageChangeListener(bottomNavigationView)

  @Provides
  @Singleton
  fun onHomePageSelectedListener(viewPager: ViewPager):
      BottomNavigationView.OnNavigationItemSelectedListener = OnHomePageSelectedListener(viewPager)

  @Provides
  @Singleton
  fun goToSettingsIntent(context: Context) = SettingsActivity.getCallingIntent(context)

  @Provides
  @Singleton
  fun rateFeature(activity: Activity) = RateFeature(
      activity,
      activity.resources,
      PreferenceManager.getDefaultSharedPreferences(activity))

  @Provides
  @Singleton
  fun filterable(activity: Activity) = activity as Filterable

  @Provides
  @Singleton
  fun filterDelegate(searchView: SearchView, filterable: Filterable) =
      FilterDelegate(searchView, filterable)
}
