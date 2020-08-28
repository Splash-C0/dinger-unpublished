package data.tinder.dislike

import com.nytimes.android.external.store3.base.impl.Store
import dagger.Lazy
import data.network.RequestSource
import reporter.CrashReporter

internal class DislikeSource(
    storeAccessor: Lazy<Store<DislikeResponse, String>>,
    crashReporter: CrashReporter)
  : RequestSource<String, DislikeResponse>(storeAccessor.get(), crashReporter)
