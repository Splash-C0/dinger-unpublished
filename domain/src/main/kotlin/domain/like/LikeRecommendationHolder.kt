package domain.like

object LikeRecommendationHolder {
  internal lateinit var likeRecommendation: LikeRecommendation

  fun likeRecommendation(it: LikeRecommendation) {
    likeRecommendation = it
  }
}
