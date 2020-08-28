package android.view

import android.content.res.Configuration
import android.graphics.Point

fun WindowManager.deviceOrientation() = if (defaultDisplay.width == defaultDisplay.height) {
  Configuration.ORIENTATION_SQUARE
} else {
  val point = Point().apply {
    defaultDisplay.getSize(this)
  }
  if (point.x < point.y) {
    Configuration.ORIENTATION_PORTRAIT
  } else {
    Configuration.ORIENTATION_LANDSCAPE
  }
}
