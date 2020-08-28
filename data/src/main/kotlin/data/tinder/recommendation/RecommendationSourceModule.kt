package data.tinder.recommendation

import android.content.Context
import com.nytimes.android.external.fs3.FileSystemRecordPersister
import com.nytimes.android.external.fs3.filesystem.FileSystemFactory
import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.impl.FluentMemoryPolicyBuilder
import com.nytimes.android.external.store3.base.impl.FluentStoreBuilder.Companion.parsedWithKey
import com.nytimes.android.external.store3.base.impl.StalePolicy
import com.nytimes.android.external.store3.base.impl.Store
import com.nytimes.android.external.store3.middleware.moshi.MoshiParserFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import dagger.Module
import dagger.Provides
import data.crash.CrashReporterModule
import data.network.ParserModule
import data.tinder.TinderApi
import data.tinder.TinderApiModule
import okio.BufferedSource
import reporter.CrashReporter
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import dagger.Lazy as DaggerLazy

@Module(includes = [
  ParserModule::class, TinderApiModule::class, CrashReporterModule::class])
internal class RecommendationSourceModule {
  @Provides
  @Singleton
  fun store(context: Context, moshiBuilder: Moshi.Builder, api: TinderApi) =
      parsedWithKey<Unit, BufferedSource, RecommendationResponse>(
          Fetcher { fetch(api) }) {
        parsers = listOf(MoshiParserFactory.createSourceParser(moshiBuilder
            .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
            .build(),
            RecommendationResponse::class.java))
        memoryPolicy = FluentMemoryPolicyBuilder.build {
          expireAfterTimeUnit = TimeUnit.MILLISECONDS
          expireAfterWrite = 1
        }
        persister = FileSystemRecordPersister.create(
            FileSystemFactory.create(context.externalCacheDir ?: context.cacheDir!!),
            { it.toString() },
            7,
            TimeUnit.DAYS)
        stalePolicy = StalePolicy.NETWORK_BEFORE_STALE
      }

  @Provides
  @Singleton
  fun source(store: DaggerLazy<Store<RecommendationResponse, Unit>>,
             crashReporter: CrashReporter) = GetRecommendationSource(store, crashReporter)

  private fun fetch(api: TinderApi) = api.getRecommendations().map { it.source() }
}
