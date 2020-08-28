package data.network

import dagger.Module
import dagger.Provides
import org.stoyicker.dinger.data.BuildConfig
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
internal class NetworkModule {
  @Provides
  fun retrofitBuilder(): Retrofit.Builder = Retrofit.Builder()
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .addConverterFactory(MoshiConverterFactory.create())
      .validateEagerly(BuildConfig.DEBUG)
}
