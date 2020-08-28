package data.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import domain.alarm.AppAlarmManager

internal class AppAlarmManagerImpl(private val context: Context)
  : AppAlarmManager() {
  override fun setOneShotBroadcastFor(requestCode: Int, notBeforeMillis: Long, task: Intent) =
      (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).setAlarmClock(
          AlarmManager.AlarmClockInfo(notBeforeMillis, null),
          generatePendingIntent(context, requestCode, task))

  override fun cancelOneShotBroadcast(requestCode: Int, task: Intent) =
      (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).cancel(
          generatePendingIntent(context, requestCode, task))

  private fun generatePendingIntent(context: Context, requestCode: Int, task: Intent) =
      PendingIntent.getBroadcast(
          context,
          requestCode,
          task,
          PendingIntent.FLAG_ONE_SHOT)
}
