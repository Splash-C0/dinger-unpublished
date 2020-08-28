package app

import android.app.ActivityManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import org.stoyicker.dinger.R

internal fun taskDescriptionFactory(context: Context) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      ActivityManager.TaskDescription(
          context.getString(R.string.app_label),
          R.mipmap.ic_launcher_adaptive,
          context.getColor(R.color.text_primary))
    } else {
      @Suppress("DEPRECATION") // Alternate not available until P
      ActivityManager.TaskDescription(
          context.getString(R.string.app_label),
          BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher_adaptive),
          context.getColor(R.color.text_primary))
    }
