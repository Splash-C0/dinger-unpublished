package data.autoswipe

import android.content.Context
import android.content.Intent
import domain.logout.AutoSwipeServiceDestructor

internal class AutoSwipeServiceDestructorImpl : AutoSwipeServiceDestructor {
  override fun stopService(context: Context) {
    context.stopService(Intent(context, AutoSwipeIntentService::class.java))
  }
}
