package data.tinder.like

import android.content.Context
import tracker.EventTracker
import tracker.TrackedDataMarshaller

internal class LikeEventTracker(
    context: Context,
    private val eventTracker: EventTracker,
    private val likeResponseMarshaller: TrackedDataMarshaller<LikeResponse>) {
  init {
    eventTracker.init(context)
  }

  fun track(response: LikeResponse) = eventTracker.trackLikeResponse(
      likeResponseMarshaller.marshall(response))
}
