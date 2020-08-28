package app.common.di

import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Named

@Module
internal class SchedulersModule {
  @Provides
  @Named("io")
  fun ioScheduler(): Scheduler = Schedulers.io()

  @Provides
  @Named("main")
  fun mainThreadScheduler(): Scheduler = AndroidSchedulers.mainThread()
}
