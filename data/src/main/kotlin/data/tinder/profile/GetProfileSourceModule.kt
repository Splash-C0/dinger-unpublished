package data.tinder.profile

import android.content.Context
import com.nytimes.android.external.fs3.FileSystemRecordPersister
import com.nytimes.android.external.fs3.filesystem.FileSystemFactory
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
internal class GetProfileSourceModule {
  @Provides
  @Singleton
  fun store(context: Context, moshiBuilder: Moshi.Builder, api: TinderApi) =
      FluentStoreBuilder.parsedWithKey<Unit, BufferedSource, GetProfileResponse>(
          Fetcher { fetch(api) }) {
        parsers = listOf(MoshiParserFactory.createSourceParser(
            moshiBuilder.build(), GetProfileResponse::class.java))
        memoryPolicy = FluentMemoryPolicyBuilder.build {
          expireAfterTimeUnit = TimeUnit.MILLISECONDS
          expireAfterWrite = 1
        }
        persister = FileSystemRecordPersister.create(
            FileSystemFactory.create(context.externalCacheDir ?: context.cacheDir!!),
            { it.toString() },
            1,
            TimeUnit.MILLISECONDS)
        stalePolicy = StalePolicy.NETWORK_BEFORE_STALE
      }

  @Provides
  @Singleton
  fun source(store: Lazy<Store<GetProfileResponse, Unit>>,
             crashReporter: CrashReporter) = GetProfileSource(store, crashReporter)

  private fun fetch(api: TinderApi) = api.getProfile().map { it.source() }
}
