package tracker

import android.content.Context
import android.os.Bundle

internal sealed class EventTrackerImpl : EventTracker {
  object Void : EventTrackerImpl() {
    override fun init(context: Context) = Unit

    override fun trackRecommendationResponse(data: Bundle) = Unit

    override fun trackLikeResponse(data: Bundle) = Unit

    override fun trackUserProvidedAccount() = Unit

    override fun setUserProvidedAccount(value: String?) = Unit
  }
}
