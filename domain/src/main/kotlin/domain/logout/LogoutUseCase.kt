package domain.logout

import android.content.Context
import android.preference.PreferenceManager
import com.facebook.login.LoginManager
import domain.autoswipe.AutoSwipeHolder
import domain.autoswipe.PostAutoSwipeUseCase
import domain.interactor.CompletableDisposableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class LogoutUseCase(
    private val context: Context,
    asyncExecutionScheduler: Scheduler? = null)
  : CompletableDisposableUseCase(asyncExecutionScheduler, Schedulers.trampoline()) {
  override fun buildUseCase(): Completable {
    return Completable.fromCallable {
      LogoutHolder.apply {
        autoswipeDestructor.stopService(context)
        alarmManager.cancelOneShotBroadcast(
            PostAutoSwipeUseCase.REQUEST_CODE,
            AutoSwipeHolder.autoSwipeLauncherFactory.newFromBroadcast(context))
        removeAccount.removeAccounts()
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().apply()
        LoginManager.getInstance().logOut()
      }
    }
  }
}
