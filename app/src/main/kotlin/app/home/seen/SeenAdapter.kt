package app.home.seen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import domain.recommendation.DomainRecommendationUser
import org.stoyicker.dinger.R

class SeenAdapter : PagedListAdapter<DomainRecommendationUser, SeenRecommendationViewHolder>(DIFF_CALLBACK) {
  override fun onCreateViewHolder(parent: ViewGroup, position: Int) =
      SeenRecommendationViewHolder(LayoutInflater.from(parent.context)
          .inflate(R.layout.item_view_recommendation, parent, false))

  override fun onBindViewHolder(holder: SeenRecommendationViewHolder, position: Int) {
    val item = getItem(position)
    if (item != null) {
      holder.bindTo(item)
    }
  }

  private companion object {
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DomainRecommendationUser>() {
      override fun areItemsTheSame(
          p0: DomainRecommendationUser,
          p1: DomainRecommendationUser) = false

      override fun areContentsTheSame(
          p0: DomainRecommendationUser,
          p1: DomainRecommendationUser) = false
    }
  }
}
