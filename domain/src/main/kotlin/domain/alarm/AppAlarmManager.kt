package domain.alarm

import android.content.Intent

abstract class AppAlarmManager {
  abstract fun setOneShotBroadcastFor(requestCode: Int, notBeforeMillis: Long, task: Intent)

  abstract fun cancelOneShotBroadcast(requestCode: Int, task: Intent)
}
