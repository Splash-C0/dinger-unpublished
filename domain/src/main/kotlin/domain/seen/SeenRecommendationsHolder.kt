package domain.seen

object SeenRecommendationsHolder {
  internal lateinit var seenRecommendations: SeenRecommendations

  fun seenRecommendations(it: SeenRecommendations) {
    seenRecommendations = it
  }
}
