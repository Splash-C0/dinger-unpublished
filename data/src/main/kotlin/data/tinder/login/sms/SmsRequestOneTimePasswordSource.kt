package data.tinder.login.sms

import com.nytimes.android.external.store3.base.impl.Store
import data.network.RequestSource
import reporter.CrashReporter
import dagger.Lazy as DaggerLazy

internal class SmsRequestOneTimePasswordSource(
    storeAccessor: DaggerLazy<Store<SmsRequestOneTimePasswordResponse, SmsRequestOneTimePasswordRequestParameters>>,
    crashReporter: CrashReporter)
  : RequestSource<SmsRequestOneTimePasswordRequestParameters, SmsRequestOneTimePasswordResponse>(storeAccessor.get(), crashReporter)
