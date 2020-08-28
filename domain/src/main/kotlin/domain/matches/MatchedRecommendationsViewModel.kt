package domain.matches

import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder

class MatchedRecommendationsViewModel : ViewModel() {
  fun filter(filter: String = "") = LivePagedListBuilder(
      MatchedRecommendationsHolder.matchedRecommendations.filter(filter), PAGE_SIZE)
      .build()
}

private const val PAGE_SIZE = 25
