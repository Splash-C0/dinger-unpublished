package app.home

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import app.home.me.MeFragment
import app.home.seen.SeenFragment

internal class HomeFragmentPagerAdapter(supportFragmentManager: FragmentManager)
  : FragmentStatePagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
  private val items = arrayOf(
//      MatchesFragment.newInstance(), SeenFragment.newInstance(), MeFragment.newInstance())
      SeenFragment.newInstance(), MeFragment.newInstance())

  override fun getItem(position: Int) = items[position]

  override fun getCount() = items.size

  override fun instantiateItem(container: ViewGroup, position: Int) =
      (super.instantiateItem(container, position) as Fragment).apply {
        items[position] = this
      }
}
