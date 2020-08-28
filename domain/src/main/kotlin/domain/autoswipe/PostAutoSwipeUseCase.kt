package domain.autoswipe

import android.content.Context
import androidx.annotation.IntRange
import domain.alarm.AlarmHolder
import domain.interactor.CompletableDisposableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler

abstract class PostAutoSwipeUseCase internal constructor(
    private val context: Context,
    postExecutionScheduler: Scheduler)
  : CompletableDisposableUseCase(postExecutionScheduler = postExecutionScheduler) {
  @IntRange(from = 0, to = Long.MAX_VALUE)
  internal abstract fun notBeforeMillis(context: Context): Long

  override fun buildUseCase(): Completable = Completable.fromCallable {
    AlarmHolder.alarmManager.setOneShotBroadcastFor(
        REQUEST_CODE,
        notBeforeMillis(context),
        AutoSwipeHolder.autoSwipeLauncherFactory.newFromBroadcast(context))
  }

  companion object {
    const val REQUEST_CODE = 2
  }
}
