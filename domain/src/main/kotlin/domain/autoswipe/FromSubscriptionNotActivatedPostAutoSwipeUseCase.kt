package domain.autoswipe

import android.content.Context
import io.reactivex.Scheduler
import java.util.Date
import java.util.concurrent.TimeUnit

class FromSubscriptionNotActivatedPostAutoSwipeUseCase(
    context: Context,
    postExecutionScheduler: Scheduler)
  : PostAutoSwipeUseCase(context, postExecutionScheduler) {
  override fun notBeforeMillis(context: Context) = interval()
}

// Somewhere between 7 and 14 days
private fun interval() = Date().time + TimeUnit.DAYS.toMillis((7..14).random().toLong())
