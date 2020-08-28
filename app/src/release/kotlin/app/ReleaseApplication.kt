package app

import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.util.Base64
import android.widget.Toast
import java.security.MessageDigest

internal class ReleaseApplication : MainApplication() {
  override fun onCreate() {
    super.onCreate()
    verifySigningCertificates()
  }

  private fun verifySigningCertificates() {
    val md = MessageDigest.getInstance(HASH_ALGORITHM)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
          .signingInfo.apkContentsSigners
    } else {
      @Suppress("DEPRECATION") // Not in this API level
      packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures
    }.forEach {
      md.update(it.toByteArray())
    }
    val currentSignature = Base64.encodeToString(md.digest(), Base64.DEFAULT)
    if (currentSignature != EXPECTED_SIGNATURE) {
      Toast.makeText(this, "Detected tampering, shutting down...", Toast.LENGTH_LONG)
          .show()
      Handler(Looper.myLooper()!!).postDelayed({
        Process.killProcess(Process.myPid())
      }, 1000)
    }
  }
}

private const val HASH_ALGORITHM = "SHA"
private const val EXPECTED_SIGNATURE = "wVYaB+hqDjggUPrXcsCAneHWT2Y=\n"
