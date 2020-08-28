package domain.dislike

object DislikeRecommendationHolder {
  internal lateinit var dislikeRecommendation: DislikeRecommendation

  fun dislikeRecommendation(it: DislikeRecommendation) {
    dislikeRecommendation = it
  }
}
