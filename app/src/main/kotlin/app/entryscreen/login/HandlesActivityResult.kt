package app.entryscreen.login

import android.content.Intent

internal interface HandlesActivityResult {
  fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean
}
