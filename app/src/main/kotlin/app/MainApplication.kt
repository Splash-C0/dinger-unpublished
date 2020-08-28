package app

import android.annotation.SuppressLint
import android.app.Application
import androidx.core.provider.FontRequest
import androidx.emoji.text.EmojiCompat
import androidx.emoji.text.FontRequestEmojiCompatConfig
import app.entryscreen.DaggerEntryScreenComponent
import app.entryscreen.EntryScreenComponent
import com.google.android.play.core.missingsplits.MissingSplitsManagerFactory
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import org.stoyicker.dinger.BuildConfig
import org.stoyicker.dinger.R
import reporter.CrashReporter
import java.util.Locale
import javax.inject.Inject

/**
 * Custom application.
 */
@SuppressLint("Registered") // It is registered in the buildtype-specific manifests
internal abstract class MainApplication : Application() {
  val entryScreenComponent: EntryScreenComponent by lazy {
    DaggerEntryScreenComponent.factory().create(this)
  }

  @Inject
  lateinit var crashReporter: CrashReporter

  override fun onCreate() {
    super.onCreate()
    DaggerMainApplicationComponent.create().inject(this)
    crashReporter.init(this)
    val uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
    Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
      val msg = exception.run { exception.localizedMessage.orEmpty() + message.orEmpty() }.toLowerCase(Locale.ENGLISH)
      when {
        msg.contains("did not then call Service.startForeground") -> Unit // swallow
        msg.contains("too many receivers") -> Unit // swallow
        else ->
          if (BuildConfig.DEBUG) {
            crashReporter.report(exception)
          } else {
            uncaughtExceptionHandler?.uncaughtException(thread, exception)
          }
      }
    }
    if (MissingSplitsManagerFactory.create(this).disableAppIfMissingRequiredSplits()) {
      crashReporter.report(Error("Install is missing required splits. App disabled."))
      return
    }
    EmojiCompat.init(FontRequestEmojiCompatConfig(this, FontRequest(
        "com.google.android.gms.fonts",
        "com.google.android.gms",
        "Noto Color Emoji Compat",
        R.array.com_google_android_gms_fonts_certs
    )))
    Picasso.setSingletonInstance(Picasso.Builder(this)
        .downloader(OkHttp3Downloader(this, Long.MAX_VALUE))
        .indicatorsEnabled(!BuildConfig.CI)
        .loggingEnabled(!BuildConfig.CI)
        .build())
    System.setProperty("http.keepAlive", "false")
  }
}
