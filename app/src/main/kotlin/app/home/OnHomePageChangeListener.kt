package app.home

import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView

internal class OnHomePageChangeListener(private val bottomNavigationView: BottomNavigationView)
  : ViewPager.OnPageChangeListener {
  override fun onPageScrollStateChanged(state: Int) = Unit

  override fun onPageScrolled(position: Int,
                              positionOffset: Float,
                              positionOffsetPixels: Int) = Unit

  override fun onPageSelected(position: Int) {
    bottomNavigationView.menu.getItem(position).isChecked = true
  }
}
