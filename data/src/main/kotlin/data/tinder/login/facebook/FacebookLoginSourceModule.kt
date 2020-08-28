package data.tinder.login.facebook

import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.impl.FluentMemoryPolicyBuilder
import com.nytimes.android.external.store3.base.impl.FluentStoreBuilder
import com.nytimes.android.external.store3.base.impl.StalePolicy
import com.nytimes.android.external.store3.base.impl.Store
import com.nytimes.android.external.store3.middleware.moshi.MoshiParserFactory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import data.crash.CrashReporterModule
import data.network.ParserModule
import data.tinder.TinderApi
import data.tinder.TinderApiModule
import okio.BufferedSource
import reporter.CrashReporter
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import dagger.Lazy as DaggerLazy

@Module(includes = [
  ParserModule::class, TinderApiModule::class, CrashReporterModule::class])
internal class FacebookLoginSourceModule {
  @Provides
  @Singleton
  fun store(moshiBuilder: Moshi.Builder, api: TinderApi) =
      FluentStoreBuilder.parsedWithKey<FacebookLoginRequestParameters, BufferedSource, FacebookLoginResponse>(
          Fetcher { fetch(it, api) }) {
        parsers = listOf(MoshiParserFactory.createSourceParser(
            moshiBuilder.build(), FacebookLoginResponse::class.java))
        memoryPolicy = FluentMemoryPolicyBuilder.build {
          expireAfterWrite = 1
          expireAfterTimeUnit = TimeUnit.SECONDS
          memorySize = 0
        }
        stalePolicy = StalePolicy.NETWORK_BEFORE_STALE
      }

  @Provides
  @Singleton
  fun source(store: DaggerLazy<Store<FacebookLoginResponse, FacebookLoginRequestParameters>>,
             crashReporter: CrashReporter) = FacebookLoginSource(store, crashReporter)

  private fun fetch(requestParameters: FacebookLoginRequestParameters, api: TinderApi) =
      api.getTinderApiTokenViaFacebook(requestParameters).map { it.source() }
}
