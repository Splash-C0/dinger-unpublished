package data.tinder.recommendation

import android.os.Bundle
import tracker.TrackedDataMarshaller
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class RecommendationResponseTrackedDataMarshaller
  : TrackedDataMarshaller<RecommendationResponse> {
  override fun marshall(source: RecommendationResponse) = Bundle().apply {
    putString("message", source.message ?: "no_message")
    putString("status", source.status.toString())
    Date().time.let {
      putLong("date", it)
      putString(
          "date_string",
          SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.ENGLISH).format(it))
    }
  }
}
