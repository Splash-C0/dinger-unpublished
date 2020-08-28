package data.tinder.login.sms

import dagger.Module
import dagger.Provides
import data.crash.CrashReporterModule
import domain.login.sms.SmsLogin
import reporter.CrashReporter
import javax.inject.Singleton

@Module(includes = [
  SmsRequestOneTimePasswordFacadeModule::class,
  SmsVerifyOneTimePasswordFacadeModule::class,
  SmsLoginFacadeModule::class,
  CrashReporterModule::class])
internal class SmsLoginModule {
  @Provides
  @Singleton
  fun login(smsRequestOneTimePasswordFacade: SmsRequestOneTimePasswordFacade,
            smsVerifyOneTimePasswordFacade: SmsVerifyOneTimePasswordFacade,
            smsLoginFacade: SmsLoginFacade,
            crashReporter: CrashReporter): SmsLogin =
      SmsLoginImpl(
          smsRequestOneTimePasswordFacade,
          smsVerifyOneTimePasswordFacade,
          smsLoginFacade,
          crashReporter)
}
