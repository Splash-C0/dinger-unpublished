package data.tinder.profile

import data.ObjectMapper
import reporter.CrashReporter

internal class GetProfileRequestObjectMapper(crashReporter: CrashReporter)
  : ObjectMapper<Unit, Unit>(crashReporter) {
  override fun fromImpl(source: Unit) = Unit
}
