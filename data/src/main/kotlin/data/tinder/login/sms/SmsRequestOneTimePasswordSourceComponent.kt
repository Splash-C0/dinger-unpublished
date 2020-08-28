package data.tinder.login.sms

import dagger.Component
import javax.inject.Singleton

@Component(modules = [SmsRequestOneTimePasswordSourceModule::class])
@Singleton
internal interface SmsRequestOneTimePasswordSourceComponent
