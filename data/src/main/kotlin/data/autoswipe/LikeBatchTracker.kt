package data.autoswipe

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import org.stoyicker.dinger.data.R

@SuppressLint("StaticFieldLeak") // App context is fine
internal object LikeBatchTracker {
  private lateinit var preferenceKey: String
  private lateinit var sharedPreferences: SharedPreferences

  fun init(context: Context) {
    preferenceKey = context.getString(R.string.preference_key_autoswipe_batch_likes)
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
  }

  fun addLike() = writePref(getPref() + 1)

  fun closeBatch() = writePref(DEFAULT_LIKE_AMOUNT)

  fun batchAdmitsMore() = getPref() <= MAX_BATCH_SIZE

  fun trackedLikes() = getPref()

  private fun writePref(value: Int) = sharedPreferences.edit()
      .putInt(preferenceKey, value)
      .apply()

  private fun getPref() = sharedPreferences.getInt(preferenceKey, DEFAULT_LIKE_AMOUNT)
}

private const val DEFAULT_LIKE_AMOUNT = 0
private const val MAX_BATCH_SIZE = 1700
