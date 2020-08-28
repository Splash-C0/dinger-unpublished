package data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import data.database.AppDatabase
import javax.inject.Singleton

@Module
internal class RootModule(private val context: Context) {
  @Provides
  @Singleton
  fun context() = context

  @Provides
  @Singleton
  fun database(context: Context): AppDatabase = Room.databaseBuilder(
      context,
      AppDatabase::class.java,
      "AppDatabase")
      // AUTOMATIC (the default) transforms into WRITE_AHEAD_LOGGING, which seems to give problems
      // with incorrectly understanding database locks
      .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
      .fallbackToDestructiveMigration()
      .allowMainThreadQueries()
      .build()
}
