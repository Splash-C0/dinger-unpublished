package data.tinder.dislike

import data.ObjectMapper
import domain.recommendation.DomainRecommendationUser
import reporter.CrashReporter

internal class DislikeRequestObjectMapper(crashReporter: CrashReporter)
  : ObjectMapper<DomainRecommendationUser, String>(crashReporter) {
  override fun fromImpl(source: DomainRecommendationUser) = source.id
}
