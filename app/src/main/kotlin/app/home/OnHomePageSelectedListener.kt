package app.home

import android.view.MenuItem
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.stoyicker.dinger.R

internal class OnHomePageSelectedListener(private val homePager: ViewPager)
  : BottomNavigationView.OnNavigationItemSelectedListener {
  override fun onNavigationItemSelected(menuItem: MenuItem) = true.also {
    homePager.currentItem = when (menuItem.itemId) {
//      R.id.navigation_item_matches -> 0
//      R.id.navigation_item_seen -> 1
//      R.id.navigation_item_me -> 2
      R.id.navigation_item_seen -> 0
      R.id.navigation_item_me -> 1
      else -> throw IllegalStateException("Unexpected menu item index selected ${menuItem.itemId}")
    }
  }
}
