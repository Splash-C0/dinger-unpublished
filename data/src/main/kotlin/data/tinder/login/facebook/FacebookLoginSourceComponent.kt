package data.tinder.login.facebook

import dagger.Component
import javax.inject.Singleton

@Component(modules = [FacebookLoginSourceModule::class])
@Singleton
internal interface FacebookLoginSourceComponent
