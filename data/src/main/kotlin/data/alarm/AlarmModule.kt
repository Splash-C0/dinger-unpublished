package data.alarm

import android.content.Context
import dagger.Module
import dagger.Provides
import data.RootModule
import domain.alarm.AppAlarmManager
import javax.inject.Singleton

@Module(includes = [RootModule::class])
internal class AlarmModule {
  @Provides
  @Singleton
  fun alarmManager(context: Context): AppAlarmManager = AppAlarmManagerImpl(context)
}
