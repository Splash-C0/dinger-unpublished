package data.autoswipe

import dagger.Module
import dagger.Provides
import domain.autoswipe.AutoSwipeLauncherFactory
import javax.inject.Singleton

@Module
internal class AutoSwipeModule {
  @Provides
  @Singleton
  fun autoSwipeIntentFactory(): AutoSwipeLauncherFactory = AutoSwipeLauncherFactoryImpl()
}
