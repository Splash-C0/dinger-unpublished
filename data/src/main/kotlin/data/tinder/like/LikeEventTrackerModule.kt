package data.tinder.like

import android.content.Context
import dagger.Module
import dagger.Provides
import data.RootModule
import data.event.VoidEventTrackerModule
import tracker.EventTracker
import javax.inject.Singleton

@Module(includes = [RootModule::class, VoidEventTrackerModule::class])
internal class LikeEventTrackerModule {
  @Provides
  @Singleton
  fun likeResponseMarshaller() = LikeResponseTrackedDataMarshaller()

  @Provides
  @Singleton
  fun eventTracker(
      context: Context,
      eventTracker: EventTracker,
      likeResponseMarshaller: LikeResponseTrackedDataMarshaller) =
      LikeEventTracker(
          context,
          eventTracker,
          likeResponseMarshaller)
}
