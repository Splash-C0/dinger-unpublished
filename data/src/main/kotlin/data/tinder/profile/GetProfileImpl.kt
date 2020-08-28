package data.tinder.profile

import domain.profile.DomainGetProfileAnswer
import domain.profile.GetProfile
import io.reactivex.Single
import reporter.CrashReporter

internal class GetProfileImpl(
    private val getProfileFacade: GetProfileFacade,
    private val crashReporter: CrashReporter) : GetProfile {
  override fun getProfile()
      : Single<DomainGetProfileAnswer> =
      getProfileFacade.fetch(Unit).doOnError { crashReporter.report(it) }
}
