package data.account

import android.content.Context
import dagger.Module
import dagger.Provides
import data.RootModule
import javax.inject.Singleton

@Module(includes = [RootModule::class])
internal class AccountModule {
  @Provides
  @Singleton
  fun accountManager(context: Context) = AppAccountAuthenticator(context)
}
