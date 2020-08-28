package data.autoswipe

import dagger.Module
import dagger.Provides
import domain.logout.AutoSwipeServiceDestructor
import javax.inject.Singleton

@Module
internal class AutoSwipeServiceDestructorModule {
  @Provides
  @Singleton
  fun autoswipeServiceDestructor(): AutoSwipeServiceDestructor = AutoSwipeServiceDestructorImpl()
}
