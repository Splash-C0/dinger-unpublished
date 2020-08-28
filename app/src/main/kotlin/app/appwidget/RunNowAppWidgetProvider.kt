package app.appwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import domain.autoswipe.AutoSwipeHolder
import domain.autoswipe.PostAutoSwipeUseCase
import org.stoyicker.dinger.R

internal class RunNowAppWidgetProvider : AppWidgetProvider() {
  override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
    val layout = RemoteViews(context.packageName, R.layout.appwidget_run_now)
    layout.setOnClickPendingIntent(
        R.id.root,
        PendingIntent.getBroadcast(
            context,
            PostAutoSwipeUseCase.REQUEST_CODE,
            AutoSwipeHolder.autoSwipeLauncherFactory.newFromBroadcast(context),
            PendingIntent.FLAG_UPDATE_CURRENT))
    appWidgetIds.forEach {
      appWidgetManager.updateAppWidget(it, layout)
    }
  }
}
