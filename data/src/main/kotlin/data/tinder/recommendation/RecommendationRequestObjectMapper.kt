package data.tinder.recommendation

import data.ObjectMapper
import reporter.CrashReporter

internal class RecommendationRequestObjectMapper(
    crashReporter: CrashReporter) : ObjectMapper<Unit, Unit>(crashReporter) {
  override fun fromImpl(source: Unit) = Unit
}
