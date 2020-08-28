package data.network

import com.squareup.moshi.classJsonAdapterFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal class JsonObjectOrFalseAdapterModule {
  @Provides
  @Singleton
  fun provideAdapterFactory() = JsonObjectOrFalseAdapter.Factory(classJsonAdapterFactory)
}
