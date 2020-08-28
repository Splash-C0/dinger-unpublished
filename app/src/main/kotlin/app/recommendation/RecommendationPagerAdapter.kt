package app.recommendation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

internal class RecommendationPagerAdapter(supportFragmentManager: FragmentManager, count: Int)
  : FragmentStatePagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
  private val items = Array<Fragment>(count) {
    newRecommendationImageFragment(it)
  }

  override fun getItem(index: Int) = items[index]

  override fun getCount() = items.size
}
