package data.tinder.dislike

import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.impl.FluentMemoryPolicyBuilder
import com.nytimes.android.external.store3.base.impl.FluentStoreBuilder
import com.nytimes.android.external.store3.base.impl.StalePolicy
import com.nytimes.android.external.store3.base.impl.Store
import com.nytimes.android.external.store3.middleware.moshi.MoshiParserFactory
import com.squareup.moshi.Moshi
import dagger.Lazy
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

@Module(includes = [
  ParserModule::class, TinderApiModule::class, CrashReporterModule::class])
internal class DislikeSourceModule {
  @Provides
  @Singleton
  fun store(moshiBuilder: Moshi.Builder, api: TinderApi) =
      FluentStoreBuilder.parsedWithKey<String, BufferedSource, DislikeResponse>(
          Fetcher { fetch(it, api) }) {
        parsers = listOf(MoshiParserFactory.createSourceParser(
            moshiBuilder.build(), DislikeResponse::class.java))
        memoryPolicy = FluentMemoryPolicyBuilder.build {
          expireAfterWrite = 1
          expireAfterTimeUnit = TimeUnit.SECONDS
          memorySize = 0
        }
        stalePolicy = StalePolicy.NETWORK_BEFORE_STALE
      }

  @Provides
  @Singleton
  fun source(store: Lazy<Store<DislikeResponse, String>>,
             crashReporter: CrashReporter) = DislikeSource(store, crashReporter)

  private fun fetch(requestParameters: String, api: TinderApi) =
      api.dislike(requestParameters).map { it.source() }
}
