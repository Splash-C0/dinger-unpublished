package data.tinder.login.sms

import com.nytimes.android.external.store3.base.impl.Store
import data.network.RequestSource
import reporter.CrashReporter
import dagger.Lazy as DaggerLazy

internal class SmsVerifyOneTimePasswordSource(
    storeAccessor: DaggerLazy<Store<SmsVerifyOneTimePasswordResponse, SmsVerifyOneTimePasswordRequestParameters>>,
    crashReporter: CrashReporter)
  : RequestSource<SmsVerifyOneTimePasswordRequestParameters, SmsVerifyOneTimePasswordResponse>(storeAccessor.get(), crashReporter)
