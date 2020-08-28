package data.network

import android.util.Log
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

@Module(includes = [CommonNetworkClientModule::class])
internal class FlavoredNetworkClientModule {
  @Provides
  fun clientBuilder(@Common clientBuilder: OkHttpClient.Builder) = clientBuilder
      .addNetworkInterceptor(HttpLoggingInterceptor(
          HttpLoggingInterceptor.Logger { message -> Log.d(TAG_DEBUG, message) }).apply {
        level = HttpLoggingInterceptor.Level.BODY
      })
}

private const val TAG_DEBUG = "request_intercepted"
