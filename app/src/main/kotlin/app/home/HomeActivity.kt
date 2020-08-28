package app.home

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import app.home.rate.RateFeature
import app.home.seen.filter.FilterDelegate
import app.taskDescriptionFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.include_home_pager.home_pager
import kotlinx.android.synthetic.main.include_navigation_bar.navigation_bar
import kotlinx.android.synthetic.main.include_toolbar.toolbar
import org.stoyicker.dinger.R
import javax.inject.Inject

internal class HomeActivity : AppCompatActivity(), Filterable {
  @Inject
  lateinit var pagerAdapter: FragmentStatePagerAdapter
  @Inject
  lateinit var viewPagerOnPageChangeListener: ViewPager.OnPageChangeListener
  @Inject
  lateinit var onNavigationItemSelectedListener: BottomNavigationView.OnNavigationItemSelectedListener
  @Inject
  lateinit var goToSettingsIntent: Intent
  @Inject
  @JvmField
  var rateFeature: RateFeature? = null
  @Inject
  @JvmField
  var filterDelegate: FilterDelegate? = null
  private var savedInstanceState: Bundle? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    this.savedInstanceState = savedInstanceState
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_home)
    setTaskDescription(taskDescriptionFactory(this))
    setSupportActionBar(toolbar)
  }

  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    this.intent = intent
    if (intent?.action == Intent.ACTION_SEARCH) {
      filterDelegate?.applyQuery(this.intent.getStringExtra(SearchManager.QUERY))
    }
  }

  override fun onCreateOptionsMenu(menu: Menu) = super.onCreateOptionsMenu(menu).also {
    menuInflater.inflate(R.menu.activity_home, menu)
    (menu.findItem(R.id.action_search).actionView as SearchView).let {
      inject(it)
      it.post { updateSearchActionVisibility(it) }
    }
  }

  override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
    R.id.action_settings -> true.also { startActivity(goToSettingsIntent) }
    else -> super.onOptionsItemSelected(item)
  }

  override fun onStop() {
    intent.apply {
      if (isChangingConfigurations) {
        putExtra(KEY_QUERY, filterDelegate?.query ?: "")
      } else {
        removeExtra(KEY_QUERY)
      }
    }
    super.onStop()
  }

  private fun setContentPager() {
    with(home_pager) {
      adapter = pagerAdapter
      addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(newScrollState: Int) =
          viewPagerOnPageChangeListener.onPageScrollStateChanged(newScrollState)

        override fun onPageScrolled(i: Int, v: Float, i1: Int) =
            viewPagerOnPageChangeListener.onPageScrolled(i, v, i1)

        override fun onPageSelected(pageIndex: Int) {
          viewPagerOnPageChangeListener.onPageSelected(pageIndex)
          updateSearchActionVisibility()
        }
      })
      offscreenPageLimit = 2
    }
    navigation_bar.setOnNavigationItemSelectedListener { menuItem ->
      updateSearchActionVisibility()
      onNavigationItemSelectedListener.onNavigationItemSelected(menuItem)
    }
  }

  private fun updateSearchActionVisibility(actionView: View? = null) =
      // We should already be inflated and injected
      (actionView ?: findViewById(R.id.action_search)!!).apply {
        // Index 0 corresponds to the 'seen' section
        visibility = if (home_pager.currentItem == 0) {
          View.VISIBLE
        } else {
          View.GONE
        }
      }

  private fun inject(searchView: SearchView) {
    DaggerHomeComponent.factory()
        .create(
            fragmentManager = supportFragmentManager,
            bottomNavigationView = navigation_bar,
            viewPager = home_pager,
            context = this,
            activity = this,
            searchView = searchView)
        .inject(this)
    onInjected(savedInstanceState)
  }

  private fun onInjected(savedInstanceState: Bundle?) {
    setContentPager()
    rateFeature?.start(savedInstanceState)
    filterDelegate?.apply {
      init(this@HomeActivity)
      applyQuery(intent.getStringExtra(KEY_QUERY))
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    rateFeature?.release(outState)
  }

  override fun onDestroy() {
    super.onDestroy()
    rateFeature?.release()
  }

  override fun getFilter(): Filter? = (pagerAdapter.getItem(0) as? Filterable)?.filter

  companion object {
    fun getCallingIntent(context: Context) = Intent(context, HomeActivity::class.java)
  }
}

private const val KEY_QUERY = "KEY_QUERY"
