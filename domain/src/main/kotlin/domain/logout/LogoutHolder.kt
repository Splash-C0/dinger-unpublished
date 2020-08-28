package domain.logout

import domain.alarm.AppAlarmManager
import domain.login.AccountManagement

object LogoutHolder {
  internal lateinit var alarmManager: AppAlarmManager
  internal lateinit var autoswipeDestructor: AutoSwipeServiceDestructor
  internal lateinit var removeAccount: AccountManagement
  lateinit var storageClear: StorageClear

  fun alarmManager(serviceAlarmManager: AppAlarmManager) {
    alarmManager = serviceAlarmManager
  }

  fun autoswipeDestructor(it: AutoSwipeServiceDestructor) {
    autoswipeDestructor = it
  }

  fun removeAccount(it: AccountManagement) {
    removeAccount = it
  }

  fun storageClear(it: StorageClear) {
    storageClear = it
  }
}
