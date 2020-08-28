package data.tinder.recommendation

import android.content.Context
import tracker.EventTracker
import tracker.TrackedDataMarshaller

internal class RecommendationEventTracker(
    context: Context,
    private val eventTracker: EventTracker,
    private val recommendationResponseMarshaller: TrackedDataMarshaller<RecommendationResponse>) {
  init {
    eventTracker.init(context)
  }

  fun track(response: RecommendationResponse) = eventTracker.trackRecommendationResponse(
      recommendationResponseMarshaller.marshall(response))
}
