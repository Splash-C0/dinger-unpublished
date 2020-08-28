package data.tinder.profile

import com.nytimes.android.external.store3.base.impl.Store
import dagger.Lazy
import data.network.RequestSource
import reporter.CrashReporter

internal class GetProfileSource(
    storeAccessor: Lazy<Store<GetProfileResponse, Unit>>,
    crashReporter: CrashReporter)
  : RequestSource<Unit, GetProfileResponse>(storeAccessor.get(), crashReporter)
