package data.tinder.login.sms

import com.nytimes.android.external.store3.base.impl.Store
import data.network.RequestSource
import reporter.CrashReporter
import dagger.Lazy as DaggerLazy

internal class SmsLoginSource(
    storeAccessor: DaggerLazy<Store<SmsLoginResponse, SmsLoginRequestParameters>>,
    crashReporter: CrashReporter)
  : RequestSource<SmsLoginRequestParameters, SmsLoginResponse>(storeAccessor.get(), crashReporter)
