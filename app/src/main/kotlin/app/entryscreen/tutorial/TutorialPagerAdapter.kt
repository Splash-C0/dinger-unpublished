package app.entryscreen.tutorial

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

internal class TutorialPagerAdapter(fragmentManager: FragmentManager)
  : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
  private val items = arrayOf(
      WelcomePageFragment.newInstance(),
      DingerIsSmartPageFragment.newInstance(),
      MoreMatchesPageFragment.newInstance()
  )

  override fun getItem(index: Int) = items[index]

  override fun getCount() = items.size
}
