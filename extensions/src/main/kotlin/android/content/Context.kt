package android.content

import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import org.stoyicker.dinger.extensions.R

fun Context.startIntent(intent: Intent, noHandlersFallback: (Intent) -> Unit = {
  Toast.makeText(
      this,
      getString(R.string.no_intent_handlers, intent),
      Toast.LENGTH_LONG)
      .show()
}) = if (packageManager.queryIntentActivities(intent, 0).size > 0) {
  startActivity(intent)
} else {
  noHandlersFallback(intent)
}

fun Context.isOnNotMeteredInternet() = getSystemService(ConnectivityManager::class.java)?.let {
  when (val activeNetworkCapabilities = it.getNetworkCapabilities(it.activeNetwork)) {
    null -> false
    else -> (activeNetworkCapabilities as NetworkCapabilities).run {
      hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED) &&
          hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
  }
} ?: false


fun Context.isPackageInstalled(packageId: String) = try {
  packageManager.getPackageInfo(packageId, 0)
  true
} catch (ignored: PackageManager.NameNotFoundException) {
  false
}

@Suppress("UNCHECKED_CAST")
fun <T> Context.safeApplication() = applicationContext as T
