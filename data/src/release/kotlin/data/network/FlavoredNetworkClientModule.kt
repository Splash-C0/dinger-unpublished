package data.network

import dagger.Binds
import dagger.Module
import okhttp3.OkHttpClient

@Module(includes = [CommonNetworkClientModule::class])
internal abstract class FlavoredNetworkClientModule {
  @Binds
  abstract fun clientBuilder(
      @Common
      clientBuilder: OkHttpClient.Builder): OkHttpClient.Builder
}
