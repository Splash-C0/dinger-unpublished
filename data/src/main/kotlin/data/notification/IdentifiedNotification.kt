package data.notification

import android.app.Notification

internal data class IdentifiedNotification(
    internal val id: Int, internal val delegate: Notification)
