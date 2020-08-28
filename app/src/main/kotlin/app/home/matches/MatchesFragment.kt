package app.home.matches

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import domain.matches.MatchedRecommendationsViewModel
import domain.recommendation.DomainRecommendationUser
import org.stoyicker.dinger.R
import javax.inject.Inject

internal class MatchesFragment : Fragment() {
  @Inject
  lateinit var viewModel: MatchedRecommendationsViewModel
  @Inject
  lateinit var observer: Observer<PagedList<DomainRecommendationUser>>
  @Inject
  lateinit var seenAdapter: PagedListAdapter<DomainRecommendationUser, MatchedRecommendationViewHolder>
  @Inject
  lateinit var layoutManager: RecyclerView.LayoutManager

  override fun onCreateView(inflater: LayoutInflater,
                            parent: ViewGroup?,
                            savedInstanceState: Bundle?) =
      inflater.inflate(R.layout.fragment_matches, parent, false)!!

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    inject(view.context, view.findViewById(android.R.id.empty))
    viewModel.filter()
        .observe(viewLifecycleOwner, observer)
    view.findViewById<RecyclerView>(R.id.recycler_view).apply {
      adapter = seenAdapter
      layoutManager = this@MatchesFragment.layoutManager
    }
  }

  private fun inject(context: Context, emptyView: View) =
      DaggerMatchesComponent.factory()
          .create(
              context = context,
              fragment = this,
              emptyView = emptyView)
          .inject(this)

  companion object {
    fun newInstance() = MatchesFragment()
  }
}
