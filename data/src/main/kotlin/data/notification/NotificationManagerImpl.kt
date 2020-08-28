package data.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.jetbrains.annotations.Contract
import org.stoyicker.dinger.data.R
import java.util.Date
import java.util.Locale

internal class NotificationManagerImpl(
    private val context: Context) : NotificationManager {
  @Contract(value = "_, _, _, _, null, true, _, _, _ -> fail")
  override fun build(
      @StringRes channelName: Int,
      @StringRes title: Int,
      @StringRes body: Int,
      @NotificationCategory category: String,
      @NotificationPriority priority: Int,
      @NotificationVisibility visibility: Int,
      clickHandler: PendingIntent?,
      actions: Array<Notification.Action>,
      notificationId: Int) = build(
      channelName = channelName,
      title = context.getString(title),
      body = context.getString(body),
      category = category,
      priority = priority,
      visibility = visibility,
      clickHandler = clickHandler,
      actions = actions,
      notificationId = notificationId)

  override fun build(
      @StringRes channelName: Int,
      title: String,
      body: String,
      bigBody: String?,
      @NotificationCategory category: String,
      @NotificationPriority priority: Int,
      @NotificationVisibility visibility: Int,
      clickHandler: PendingIntent?,
      actions: Array<Notification.Action>,
      notificationId: Int): IdentifiedNotification {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      context.getSystemService(android.app.NotificationManager::class.java)
          ?.createNotificationChannel(NotificationChannel(
              getChannelId(context, channelName),
              context.getString(channelName), getChannelImportance(priority)).apply {
            importance = android.app.NotificationManager.IMPORTANCE_LOW
            enableVibration(false)
            enableLights(false)
            setSound(null, null) // No sound
          })
    }
    @Suppress("DEPRECATION") // Deprecated from API 26 on, not before
    return IdentifiedNotification(notificationId, Notification.Builder(context)
        .setAutoCancel(true)
        .setContentIntent(clickHandler)
        .setContentText(body)
        .setContentTitle(title)
        .setDefaults(0)
        .setOngoing(false)
        .setOnlyAlertOnce(true)
        .setPriority(priority)
        .setSmallIcon(R.drawable.ic_notification)
        .setTicker(body)
        .setStyle(Notification.BigTextStyle()
            .bigText(bigBody))
        .setShowWhen(true)
        .apply {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            setLocalOnly(false)
            setSortKey("${Date().time}")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
              setColor(Color.parseColor("#FFAB40")) // textPrimary, not available in data
              setCategory(NotificationCompat.CATEGORY_SERVICE)
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setBadgeIconType(NotificationCompat.BADGE_ICON_NONE)
                setChannelId(getChannelId(context, channelName))
                setGroupAlertBehavior(
                    NotificationCompat.GROUP_ALERT_SUMMARY)
              }
              setVisibility(visibility)
            }
          }
          actions.forEach { addAction(it) }
        }
        .build())
  }

  override fun show(notification: IdentifiedNotification) =
      NotificationManagerCompat.from(context).notify(notification.id, notification.delegate)

  override fun cancel(notificationId: Int) =
      NotificationManagerCompat.from(context).cancel(notificationId)

  /**
   * See https://stackoverflow.com/questions/36634008/why-notificationmanagercompatcancelall-gets-securityexception
   */
  override fun cancelAll() = try {
    NotificationManagerCompat.from(context).cancelAll()
  } catch (ignored: SecurityException) {
  }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun getChannelImportance(@NotificationPriority priority: Int) = when (priority) {
  NotificationManager.PRIORITY_LOW -> android.app.NotificationManager.IMPORTANCE_LOW
  NotificationManager.PRIORITY_MEDIUM -> android.app.NotificationManager.IMPORTANCE_DEFAULT
  NotificationManager.PRIORITY_HIGH -> android.app.NotificationManager.IMPORTANCE_HIGH
  else -> throw IllegalArgumentException("Illegal priority $priority")
}

/**
 * Required because a channel name may (and should) be localized, but the id must not be.
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
private fun getChannelId(context: Context, @StringRes channelName: Int) =
    Configuration(context.resources.configuration).run {
      setLocale(Locale.ENGLISH)
      context.createConfigurationContext(this).resources.getString(channelName)
    }
