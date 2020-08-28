package app.home.seen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import app.settings.EXTRA_KEY_CLICK_PREFERENCE
import app.settings.SettingsActivity
import domain.recommendation.DomainRecommendationUser
import domain.seen.SeenRecommendationsViewModel
import iaps.PurchaseManager
import iaps.SKU_ACTIVATION_STATE_ACTIVATED
import iaps.SKU_ACTIVATION_STATE_NOT_ACTIVATED
import iaps.isVip
import kotlinx.android.synthetic.main.fragment_seen.empty
import kotlinx.android.synthetic.main.fragment_seen.empty_text
import kotlinx.android.synthetic.main.fragment_seen.promo_banner_autoswipe
import kotlinx.android.synthetic.main.fragment_seen.promo_banner_autoswipe_wrapper
import kotlinx.android.synthetic.main.fragment_seen.recycler_view
import kotlinx.android.synthetic.main.fragment_seen.swipe_to_refresh
import org.stoyicker.dinger.R
import javax.inject.Inject

internal class SeenFragment : Fragment(), Filterable, SwipeRefreshLayout.OnRefreshListener {
  @Inject
  lateinit var viewModel: SeenRecommendationsViewModel

  @Inject
  lateinit var observer: Observer<PagedList<DomainRecommendationUser>>

  @Inject
  lateinit var seenAdapter: PagedListAdapter<DomainRecommendationUser, SeenRecommendationViewHolder>

  @Inject
  lateinit var layoutManager: RecyclerView.LayoutManager
  private val filter by lazy {
    object : Filter() {
      override fun performFiltering(constraint: CharSequence?) = null

      override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        filterQuery = constraint ?: ""
        onRefresh()
      }
    }
  }
  private lateinit var filterQuery: CharSequence
  private var liveData: LiveData<PagedList<DomainRecommendationUser>>? = null

  override fun onCreateView(inflater: LayoutInflater,
                            parent: ViewGroup?,
                            savedInstanceState: Bundle?) =
      inflater.inflate(R.layout.fragment_seen, parent, false)!!

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    inject(view.context, view.findViewById(android.R.id.empty))
    swipe_to_refresh.apply {
      setOnRefreshListener(this@SeenFragment)
      setColorSchemeResources(R.color.accent)
      setProgressBackgroundColorSchemeResource(R.color.primary_dark)
    }
    filterQuery = savedInstanceState?.getString(KEY_FILTER, null) ?: ""
    onRefresh()
    recycler_view.apply {
      adapter = seenAdapter
      layoutManager = this@SeenFragment.layoutManager
    }
    View.OnClickListener {
      if (PurchaseManager.getAutoswipeSubscriptionState() == SKU_ACTIVATION_STATE_ACTIVATED) {
        return@OnClickListener
      }
      startActivity(Intent(it.context, SettingsActivity::class.java)
          .putExtra(EXTRA_KEY_CLICK_PREFERENCE, R.string.preference_key_autoswipe_enabled))
    }.let {
      promo_banner_autoswipe.setOnClickListener(it)
      empty.setOnClickListener(it)
      empty_text.setOnClickListener(it)
    }
    when (PurchaseManager.getAutoswipeSubscriptionState()) {
      SKU_ACTIVATION_STATE_NOT_ACTIVATED -> promo_banner_autoswipe_wrapper.visibility = View.VISIBLE
      SKU_ACTIVATION_STATE_ACTIVATED ->
        if (isVip(promo_banner_autoswipe_wrapper.context)) {
          promo_banner_autoswipe_wrapper.visibility = View.VISIBLE
        } else {
          promo_banner_autoswipe_wrapper.visibility = View.GONE
        }
    }
  }

  override fun onResume() {
    super.onResume()
    val currentList = seenAdapter.currentList
    if (currentList == null || currentList.isEmpty()) {
      onRefresh()
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putCharSequence(KEY_FILTER, filterQuery)
  }

  override fun getFilter() = filter

  override fun onRefresh() = refreshFilter()

  private fun refreshFilter() {
    if (view == null) {
      //Illegal state
      return
    }
    if (liveData != null) {
      liveData!!.removeObservers(this)
    }
    liveData = viewModel.filter(filterQuery).apply {
      observe(viewLifecycleOwner, observer)
    }
    viewModel.filter(filterQuery).value?.let { observer.onChanged(it) }
    swipe_to_refresh.isRefreshing = false
  }

  private fun inject(context: Context, emptyView: View) =
      DaggerSeenComponent.factory()
          .create(
              context = context,
              fragment = this,
              emptyView = emptyView)
          .inject(this)

  companion object {
    fun newInstance() = SeenFragment()
  }
}

private const val KEY_FILTER = "KEY_FILTER"
