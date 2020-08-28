package domain.autoswipe

import android.content.Context
import io.reactivex.Scheduler
import java.util.Date

class ImmediatePostAutoSwipeUseCase(
    context: Context,
    postExecutionScheduler: Scheduler)
  : PostAutoSwipeUseCase(context, postExecutionScheduler) {
  override fun notBeforeMillis(context: Context) = Date().time
}
