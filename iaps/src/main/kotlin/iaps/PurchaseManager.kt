package iaps

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.isPackageInstalled
import androidx.annotation.IntDef
import java.lang.ref.WeakReference

@SuppressLint("StaticFieldLeak") // I'm holding the app context which is going to be retained anyway
object PurchaseManager {
  private lateinit var context: Context
  lateinit var activityForPurchaseFlow: WeakReference<Activity>
  var trialPeriodOver = false

  internal fun context(context: Context) {
    this.context = context.applicationContext
  }

  @SuppressLint("Recycle") // False positive
  @SkuActivationState
  fun getAutoswipeSubscriptionState(): Int {
    val cursor = context.contentResolver.query(URI_ACTIVE_PURCHASES, null, null, null, null)!!
    val ret = if (cursor.let {
          generateSequence {
            if (it.moveToNext()) it else null
          }
        }.any { it.getString(it.getColumnIndex(COLUMN_KEY_SKU)) == SKU_AUTOSWIPE_0 } || isVip(context)) {
      SKU_ACTIVATION_STATE_ACTIVATED
    } else {
      SKU_ACTIVATION_STATE_NOT_ACTIVATED
    }
    cursor.close()
    return ret
  }
}

@IntDef(value = [
  SKU_ACTIVATION_STATE_NOT_AVAILABLE,
  SKU_ACTIVATION_STATE_NOT_ACTIVATED,
  SKU_ACTIVATION_STATE_ACTIVATED])
@Retention(AnnotationRetention.SOURCE)
annotation class SkuActivationState

const val SKU_ACTIVATION_STATE_NOT_AVAILABLE = -1
const val SKU_ACTIVATION_STATE_NOT_ACTIVATED = -2
const val SKU_ACTIVATION_STATE_ACTIVATED = -3

const val SKU_AUTOSWIPE_0 = "autoswipe_0"

fun isVip(context: Context) = context.isPackageInstalled("org.jorge.lbudget")
