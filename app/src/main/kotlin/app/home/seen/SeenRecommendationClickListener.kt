package app.home.seen

import android.view.View
import app.recommendation.recommendationActivityCallingIntent

internal class SeenRecommendationClickListener(private val id: String) : View.OnClickListener {
  override fun onClick(v: View?) =
      v?.context?.let { it.startActivity(recommendationActivityCallingIntent(it, id)) } ?: Unit
}
