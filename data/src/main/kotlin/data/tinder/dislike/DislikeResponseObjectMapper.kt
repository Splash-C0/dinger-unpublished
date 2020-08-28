package data.tinder.dislike

import data.ObjectMapper
import domain.dislike.DomainDislikedRecommendationAnswer
import reporter.CrashReporter

internal class DislikeResponseObjectMapper(crashReporter: CrashReporter)
  : ObjectMapper<DislikeResponse, DomainDislikedRecommendationAnswer>(crashReporter) {
  override fun fromImpl(source: DislikeResponse) =
      DomainDislikedRecommendationAnswer(source.status in 200..299)
}
