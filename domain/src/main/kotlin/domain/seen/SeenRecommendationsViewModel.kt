package domain.seen

import androidx.lifecycle.ViewModel

class SeenRecommendationViewModel : ViewModel() {
  fun get(id: String?) = when (id) {
    null -> null
    else -> SeenRecommendationHolder.seenRecommendation.get(id)
  }
}
