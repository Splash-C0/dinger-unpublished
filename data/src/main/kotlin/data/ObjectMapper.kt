package data

import reporter.CrashReporter

internal abstract class ObjectMapper<in From : Any, out To : Any>(
    private val crashReporter: CrashReporter) {
  fun from(source: From?): To? {
    if (source == null) {
      return null
    }
    return try {
      fromImpl(source)
    } catch (fieldDeclaredNonNullIsNull: KotlinNullPointerException) {
      crashReporter.report(fieldDeclaredNonNullIsNull)
      null
    }
  }

  protected abstract fun fromImpl(source: From): To
}
