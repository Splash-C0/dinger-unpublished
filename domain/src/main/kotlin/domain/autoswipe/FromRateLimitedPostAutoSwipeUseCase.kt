package domain.autoswipe

import android.content.Context
import io.reactivex.Scheduler

class FromRateLimitedPostAutoSwipeUseCase(
    context: Context,
    postExecutionScheduler: Scheduler,
    private val notBeforeMillis: Long)
  : PostAutoSwipeUseCase(context, postExecutionScheduler) {
  override fun notBeforeMillis(context: Context) = notBeforeMillis
}
