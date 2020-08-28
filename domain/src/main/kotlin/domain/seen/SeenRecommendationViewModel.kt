package domain.seen

import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder

class SeenRecommendationsViewModel : ViewModel() {
  fun filter(filter: CharSequence) = LivePagedListBuilder(
      SeenRecommendationsHolder.seenRecommendations.filter(filter.toString()), PAGE_SIZE)
      .build()
}

private const val PAGE_SIZE = 25
