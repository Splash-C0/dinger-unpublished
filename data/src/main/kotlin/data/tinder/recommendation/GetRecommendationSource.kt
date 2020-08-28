package data.tinder.recommendation

import com.nytimes.android.external.store3.base.impl.Store
import data.network.RequestSource
import reporter.CrashReporter
import dagger.Lazy as DaggerLazy

internal class GetRecommendationSource(
    storeAccessor: DaggerLazy<Store<RecommendationResponse, Unit>>,
    crashReporter: CrashReporter)
  : RequestSource<Unit, RecommendationResponse>(storeAccessor.get(), crashReporter)
