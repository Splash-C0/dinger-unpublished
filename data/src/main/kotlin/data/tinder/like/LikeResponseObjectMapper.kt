package data.tinder.like

import data.ObjectMapper
import domain.like.DomainLikedRecommendationAnswer
import reporter.CrashReporter

internal class LikeResponseObjectMapper(
    crashReporter: CrashReporter,
    private val eventTracker: LikeEventTracker)
  : ObjectMapper<LikeResponse, DomainLikedRecommendationAnswer>(crashReporter) {
  override fun fromImpl(source: LikeResponse) = source.let {
    eventTracker.track(it)
    DomainLikedRecommendationAnswer(it.match != null, it.rateLimitedUntil)
  }
}
