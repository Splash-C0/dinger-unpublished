package tracker

import android.content.Context
import android.os.Bundle

interface EventTracker {
  fun init(context: Context)

  fun trackRecommendationResponse(data: Bundle)

  fun trackLikeResponse(data: Bundle)

  fun trackUserProvidedAccount()

  fun setUserProvidedAccount(value: String?)
}
