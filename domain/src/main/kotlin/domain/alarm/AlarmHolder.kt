package domain.alarm

object AlarmHolder {
  internal lateinit var alarmManager: AppAlarmManager

  fun alarmManager(serviceAlarmManager: AppAlarmManager) {
    this.alarmManager = serviceAlarmManager
  }
}
