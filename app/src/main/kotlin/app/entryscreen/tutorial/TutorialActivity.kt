package app.entryscreen.tutorial

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import app.entryscreen.login.TinderLoginActivity
import kotlinx.android.synthetic.main.activity_tutorial.indicator
import kotlinx.android.synthetic.main.activity_tutorial.pager_tutorial
import org.stoyicker.dinger.R

internal class TutorialActivity : AppCompatActivity(), TutorialInteractions {
  private lateinit var pager: ViewPager
  private lateinit var adapter: FragmentStatePagerAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_tutorial)
    pager = pager_tutorial.apply {
      adapter = TutorialPagerAdapter(supportFragmentManager).apply {
        this@TutorialActivity.adapter = this
      }
      indicator.setViewPager(this)
    }
  }

  override fun onNextRequested() = pager.run {
    if (currentItem < this@TutorialActivity.adapter.count - 1) {
      currentItem++
    } else {
      onTutorialFinished()
    }
    Unit
  }

  private fun onTutorialFinished() {
    TinderLoginActivity.getCallingIntent(this).apply {
      flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
      startActivity(this)
    }
    supportFinishAfterTransition()
  }
}

internal fun tutorialActivityCallingIntent(context: Context) =
    Intent(context, TutorialActivity::class.java)
