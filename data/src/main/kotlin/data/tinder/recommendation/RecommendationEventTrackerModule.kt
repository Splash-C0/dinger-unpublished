package data.tinder.recommendation

import android.content.Context
import dagger.Module
import dagger.Provides
import data.RootModule
import data.event.VoidEventTrackerModule
import tracker.EventTracker
import javax.inject.Singleton

@Module(includes = [RootModule::class, VoidEventTrackerModule::class])
internal class RecommendationEventTrackerModule {
  @Provides
  @Singleton
  fun recommendationResponseMarshaller() = RecommendationResponseTrackedDataMarshaller()

  @Provides
  @Singleton
  fun eventTracker(
      context: Context,
      eventTracker: EventTracker,
      recommendationResponseTrackedDataMarshaller: RecommendationResponseTrackedDataMarshaller) =
      RecommendationEventTracker(
          context,
          eventTracker,
          recommendationResponseTrackedDataMarshaller)
}
