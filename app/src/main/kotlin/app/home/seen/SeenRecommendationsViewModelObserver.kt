package app.home.seen

import android.view.View
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import domain.recommendation.DomainRecommendationUser

internal class SeenRecommendationsViewModelObserver(
    private val emptyView: View,
    private val adapter: PagedListAdapter<DomainRecommendationUser, SeenRecommendationViewHolder>)
  : Observer<PagedList<DomainRecommendationUser>> {
  override fun onChanged(list: PagedList<DomainRecommendationUser>?) = adapter.submitList(list.also {
    if (it.isNullOrEmpty()) {
      emptyView.visibility = View.VISIBLE
    } else {
      emptyView.visibility = View.INVISIBLE
    }
  })
}
