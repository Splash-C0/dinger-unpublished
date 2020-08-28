package data.autoswipe

import android.app.Notification
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.annotation.IntDef
import data.notification.IdentifiedNotification
import data.notification.NotificationManager
import domain.like.DomainLikedRecommendationAnswer
import org.stoyicker.dinger.data.R
import reporter.CrashReporter
import java.util.Date

internal class AutoSwipeReportHandler(
    context: Context,
    private val notificationManager: NotificationManager) {
  private var matchCounter = 0

  init {
    LikeBatchTracker.init(context)
  }

  fun addLikeAnswer(answer: DomainLikedRecommendationAnswer) {
    // The last like done comes with rateLimited too
    if (answer.rateLimitedUntilMillis == null && answer.matched) {
      addMatch()
    }
  }

  private fun addMatch() {
    ++matchCounter
  }

  fun showReport(
      context: Context,
      crashReporter: CrashReporter,
      scheduledFor: Long?,
      errorMessage: String?,
      @AutoSwipeResult result: Int) {
    val newNotification = build(context, crashReporter, scheduledFor, errorMessage, result)
    val updatedNotification = IdentifiedNotification(
        NotificationManager.ID_AUTOSWIPE, newNotification.delegate)
    notificationManager.show(updatedNotification)
  }

  fun showNeedsSubscription(context: Context) {
    val newNotification = notificationManager.build(
        channelName = R.string.autoswipe_notification_channel_name,
        title = context.getString(R.string.autoswipe_notification_title_not_subscribed),
        body = context.getString(R.string.autoswipe_notification_body_not_subscribed),
        category = NotificationManager.CATEGORY_SERVICE,
        priority = NotificationManager.PRIORITY_LOW,
        clickHandler = PendingIntent.getActivity(
            context,
            0,
            Intent().setComponent(ComponentName(
                context, "app.entryscreen.splash.SplashActivity"))
                .putExtra(EXTRA_KEY_CLICK_PREFERENCE, R.string.preference_key_autoswipe_enabled),
            FLAG_UPDATE_CURRENT),
        actions = emptyArray(),
        notificationId = NotificationManager.ID_AUTOSWIPE)
    val updatedNotification = IdentifiedNotification(
        NotificationManager.ID_AUTOSWIPE, newNotification.delegate)
    notificationManager.show(updatedNotification)
  }

  fun buildPlaceHolder(context: Context, crashReporter: CrashReporter) =
      build(
          context,
          crashReporter,
          null,
          null,
          RESULT_PLACEHOLDER)

  private fun build(
      context: Context,
      crashReporter: CrashReporter,
      scheduledFor: Long?,
      errorMessage: String?,
      @AutoSwipeResult result: Int) =
      notificationManager.build(
          channelName = R.string.autoswipe_notification_channel_name,
          title = generateTitle(context, LikeBatchTracker.trackedLikes()),
          body = generateBody(
              context,
              crashReporter,
              if (scheduledFor == null) null else Date(scheduledFor).toString(),
              errorMessage,
              result),
          category = NotificationManager.CATEGORY_SERVICE,
          priority = NotificationManager.PRIORITY_LOW,
          clickHandler = PendingIntent.getActivity(
              context,
              0,
              Intent().setComponent(ComponentName(
                  context, "app.entryscreen.splash.SplashActivity"))
                  .putExtra(EXTRA_KEY_CLICK_PREFERENCE, R.string.preference_key_autoswipe_enabled),
              FLAG_UPDATE_CURRENT),
          actions = if (result !in arrayOf(RESULT_MORE_AVAILABLE, RESULT_PLACEHOLDER)) {
            arrayOf(Notification.Action.Builder(
                null,
                context.getText(R.string.autoswipe_notification_action_run_now),
                PendingIntent.getBroadcast(
                    context,
                    0,
                    AutoSwipeLauncherBroadcastReceiver.getCallingIntent(context)
                        .putExtra(EXTRA_KEY_CLOSE_BATCH, true),
                    FLAG_UPDATE_CURRENT))
                .build())
          } else {
            emptyArray()
          },
          notificationId = NotificationManager.ID_AUTOSWIPE)

  fun clearAllNotifications() = notificationManager.cancelAll()

  companion object {
    const val RESULT_RATE_LIMITED = 1
    const val RESULT_MORE_AVAILABLE = 2
    const val RESULT_ERROR = 3
    const val RESULT_BATCH_CLOSED = 4
    private const val RESULT_PLACEHOLDER = -1


    @Retention(AnnotationRetention.SOURCE)
    @IntDef(RESULT_RATE_LIMITED,
        RESULT_MORE_AVAILABLE,
        RESULT_ERROR,
        RESULT_BATCH_CLOSED,
        RESULT_PLACEHOLDER)
    internal annotation class AutoSwipeResult

    private fun generateBody(
        context: Context,
        crashReporter: CrashReporter,
        scheduledFor: String?,
        errorMessage: String?,
        @AutoSwipeResult result: Int) = when (result) {
      RESULT_RATE_LIMITED -> context.getString(
          R.string.autoswipe_notification_body_capped, scheduledFor)
      RESULT_MORE_AVAILABLE -> context.getString(R.string.autoswipe_notification_body_more_available)
      RESULT_ERROR -> {
        crashReporter.report(Error(errorMessage ?: "Null error message"))
        context.getString(R.string.autoswipe_notification_body_error, scheduledFor)
      }
      RESULT_BATCH_CLOSED -> context.getString(
          R.string.autoswipe_notification_body_batch_closed, scheduledFor)
      RESULT_PLACEHOLDER -> context.getString(R.string.autoswipe_notification_body_placeholder)
      else -> throw IllegalStateException("Unexpected result $result in the autoswipe report.")
    }
  }
}

private fun generateTitle(context: Context, likes: Int) = StringBuilder().apply {
  append(context.resources.getQuantityString(
      R.plurals.autoswipe_notification_title_swept, likes, likes))
}.toString()

// Must match SettingsPreferenceFragmentCompat's
private const val EXTRA_KEY_CLICK_PREFERENCE = "EXTRA_KEY_CLICK_PREFERENCE"
