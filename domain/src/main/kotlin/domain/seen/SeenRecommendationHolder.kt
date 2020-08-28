package domain.seen

object SeenRecommendationHolder {
  internal lateinit var seenRecommendation: SeenRecommendation

  fun seenRecommendation(it: SeenRecommendation) {
    seenRecommendation = it
  }
}
