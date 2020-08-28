package data.tinder.login.facebook

import com.nytimes.android.external.store3.base.impl.Store
import data.network.RequestSource
import reporter.CrashReporter
import dagger.Lazy as DaggerLazy

internal class FacebookLoginSource(
    storeAccessor: DaggerLazy<Store<FacebookLoginResponse, FacebookLoginRequestParameters>>,
    crashReporter: CrashReporter)
  : RequestSource<FacebookLoginRequestParameters, FacebookLoginResponse>(storeAccessor.get(), crashReporter)
