package data.storage

import android.app.ActivityManager
import android.content.Context
import domain.logout.StorageClear

internal class StorageClearImpl : StorageClear {
  override fun clearStorage(context: Context) {
    (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
        .clearApplicationUserData()
  }
}
