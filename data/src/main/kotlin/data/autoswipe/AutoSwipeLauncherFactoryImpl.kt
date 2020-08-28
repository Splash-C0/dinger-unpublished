package data.autoswipe

import android.content.Context
import domain.autoswipe.AutoSwipeLauncherFactory

internal class AutoSwipeLauncherFactoryImpl : AutoSwipeLauncherFactory {
  override fun newFromBroadcast(context: Context) =
      AutoSwipeLauncherBroadcastReceiver.getCallingIntent(context)
}
