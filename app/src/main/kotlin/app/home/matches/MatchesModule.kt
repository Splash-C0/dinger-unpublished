package app.home.matches

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import app.home.HomeScreenScope
import dagger.Module
import dagger.Provides
import domain.matches.MatchedRecommendationsViewModel
import domain.recommendation.DomainRecommendationUser
import org.stoyicker.dinger.R

@Module
internal class MatchesModule {
  @Provides
  @HomeScreenScope
  fun viewModel(fragment: Fragment) =
      ViewModelProvider(fragment).get(MatchedRecommendationsViewModel::class.java)

  @Provides
  @HomeScreenScope
  fun adapter(): PagedListAdapter<DomainRecommendationUser, MatchedRecommendationViewHolder> =
      MatchesAdapter()

  @Provides
  @HomeScreenScope
  fun observer(
      emptyView: View,
      adapter: PagedListAdapter<DomainRecommendationUser, MatchedRecommendationViewHolder>):
      Observer<PagedList<DomainRecommendationUser>> =
      MatchedRecommendationsViewModelObserver(emptyView, adapter)

  @Provides
  @HomeScreenScope
  fun layoutManager(context: Context): RecyclerView.LayoutManager = StaggeredGridLayoutManager(
      context.resources.getInteger(R.integer.grid_span), StaggeredGridLayoutManager.VERTICAL).apply {
    gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
  }
}
