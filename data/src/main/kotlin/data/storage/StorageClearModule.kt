package data.storage

import dagger.Module
import dagger.Provides
import domain.logout.StorageClear
import javax.inject.Singleton

@Module
internal class StorageClearModule {
  @Provides
  @Singleton
  fun storageClear(): StorageClear = StorageClearImpl()
}
