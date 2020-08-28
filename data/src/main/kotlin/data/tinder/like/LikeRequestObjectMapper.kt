package data.tinder.like

import android.content.Context
import android.preference.PreferenceManager
import data.ObjectMapper
import domain.recommendation.DomainRecommendationUser
import org.stoyicker.dinger.domain.R
import reporter.CrashReporter

internal class LikeRequestObjectMapper(
    crashReporter: CrashReporter,
    private val context: Context)
  : ObjectMapper<DomainRecommendationUser, LikeRequestParameters>(crashReporter) {
  override fun fromImpl(source: DomainRecommendationUser) = LikeRequestParameters(
      source.id,
      LikeRatingRequest(
          contentHash = source.contentHash,
          photoId = source.photos?.firstOrNull()?.id ?: "",
          placeId = source.jobs.firstOrNull()?.id ?: source.schools.firstOrNull()?.id ?: "",
          sNumber = source.sNumber,
          isTopPicks = PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
              context.getString(R.string.preference_key_force_top_pick), true))
  )
}
