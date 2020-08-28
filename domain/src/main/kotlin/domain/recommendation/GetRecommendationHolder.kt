package domain.recommendation

object GetRecommendationHolder {
  internal lateinit var getRecommendation: GetRecommendation

  fun getRecommendation(it: GetRecommendation) {
    getRecommendation = it
  }
}
