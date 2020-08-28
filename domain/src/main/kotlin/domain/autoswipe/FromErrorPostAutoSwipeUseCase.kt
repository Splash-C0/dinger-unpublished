package domain.autoswipe

import android.content.Context
import io.reactivex.Scheduler
import org.stoyicker.dinger.domain.R
import java.util.Date

class FromErrorPostAutoSwipeUseCase(
    context: Context,
    postExecutionScheduler: Scheduler)
  : PostAutoSwipeUseCase(context, postExecutionScheduler) {
  companion object {
    fun interval(context: Context) = Date().time +
        context.resources.getInteger(R.integer.sweep_from_error_delay_ms).toLong()
  }

  override fun notBeforeMillis(context: Context) = interval(context)
}
