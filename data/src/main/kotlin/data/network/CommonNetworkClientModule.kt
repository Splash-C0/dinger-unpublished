package data.network

import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

@Module
internal class CommonNetworkClientModule {
  @Provides
  @Common
  fun clientBuilder(
      @Local sslSocketFactory: SSLSocketFactory,
      @Local x509TrustManager: X509TrustManager) = OkHttpClient.Builder()
      .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
      .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
      .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
      .sslSocketFactory(sslSocketFactory, x509TrustManager)

  @Provides
  @Singleton
  @Local
  fun sslSocketFactory(@Local trustManager: X509TrustManager) = SSLContext.getInstance("SSL")
      .apply {
        init(null, arrayOf(trustManager), SecureRandom())
      }.socketFactory

  @Provides
  @Singleton
  @Local
  fun x509TrustManager() = object : X509TrustManager {
    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) =
        Unit

    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) =
        Unit

    override fun getAcceptedIssuers() = emptyArray<X509Certificate>()
  }

  @Retention(AnnotationRetention.RUNTIME)
  @Qualifier
  private annotation class Local
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
internal annotation class Common

private const val TIMEOUT_SECONDS = 15L
