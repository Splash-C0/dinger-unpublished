package iaps

import android.annotation.SuppressLint
import android.content.ContentProvider
import android.content.ContentProviderOperation
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.preference.PreferenceManager
import android.widget.Toast
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.AcknowledgePurchaseResponseListener
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.google.android.play.core.missingsplits.MissingSplitsManagerFactory
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.stoyicker.dinger.iaps.R
import reporter.CrashReporters
import java.util.concurrent.TimeUnit

internal class BillingContentProvider
  : ContentProvider(),
    BillingClientStateListener,
    PurchasesUpdatedListener,
    AcknowledgePurchaseResponseListener {
  private val crashReporter = CrashReporters.bugsnag()
  private lateinit var dbHelper: SQLiteOpenHelper
  private var billingClient: BillingClient? = null

  override fun onCreate(): Boolean {
    crashReporter.init(context!!)
    if (MissingSplitsManagerFactory.create(context!!).disableAppIfMissingRequiredSplits()) {
      crashReporter.report(Error("Install is missing required splits. App disabled."))
      return false
    }
    AUTHORITY = context!!.getString(R.string.provider_authority_iaps)
    URI_PRODUCTS = Uri.parse("$PREFIX_CONTENT$AUTHORITY/$DATA_TYPE_PRODUCTS")
    URI_ACTIVE_PURCHASES = Uri.parse("$PREFIX_CONTENT$AUTHORITY/$DATA_TYPE_ACTIVE_PURCHASES")
    URI_COMMAND_PURCHASE = Uri.parse("$PREFIX_CONTENT$AUTHORITY/$COMMAND_PRODUCT_PURCHASE")
    URI_MATCHER.apply {
      addURI(AUTHORITY, DATA_TYPE_ACTIVE_PURCHASES, CODE_ACTIVE_PURCHASE_LIST)
      addURI(AUTHORITY, DATA_TYPE_PRODUCTS, CODE_PRODUCT_LIST)
      addURI(AUTHORITY, "$DATA_TYPE_PRODUCTS/*", CODE_PRODUCT)
      addURI(AUTHORITY, "$COMMAND_PRODUCT_PURCHASE/*", CODE_PRODUCT_PURCHASE)
    }
    PurchaseManager.context(context!!)
    HandlerThread(BillingContentProvider::class.java.simpleName).apply {
      start()
      BillingClientHandler(looper).sendEmptyMessage(0)
    }
    return true
  }

  inner class BillingClientHandler(looper: Looper) : Handler(looper) {
    override fun handleMessage(msg: Message) {
      dbHelper = IAPsSQLiteDao(context!!).apply {
        writableDatabase
      }
      billingClient?.endConnection()
      billingClient = BillingClient.newBuilder(context!!)
          .enablePendingPurchases()
          .setListener(this@BillingContentProvider)
          .build().apply {
            try {
              startConnection(this@BillingContentProvider)
            } catch (ignored: RuntimeException) {
            }
          }
    }
  }

  override fun onBillingSetupFinished(billingResult: BillingResult) = when (billingResult.responseCode) {
    BillingClient.BillingResponseCode.OK -> if (billingClient?.isFeatureSupported(
            BillingClient.FeatureType.SUBSCRIPTIONS)?.responseCode == BillingClient.BillingResponseCode.OK) {
      queryBillingClientForSkuDetails()
    } else {
      Toast.makeText(context, R.string.suscriptions_not_supported, Toast.LENGTH_LONG).show()
    }
    BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> billingClient?.endConnection()
        ?: Unit // Nothing we can do
    else ->
      /**
       * Re-trigger the billing setup after a second.
       */
      HandlerThread(BillingContentProvider::class.java.simpleName).run {
        start()
        BillingClientHandler(looper).apply {
          postDelayed({ sendEmptyMessage(0) }, 1000L)
        }
        Unit
      }
  }

  private fun queryBillingClientForSkuDetails() {
    billingClient?.querySkuDetailsAsync(SkuDetailsParams.newBuilder()
        .setSkusList(listOf(SKU_AUTOSWIPE_0))
        .setType(BillingClient.SkuType.SUBS)
        .build()) { queryBillingResult, skuDetailsList ->
      when (queryBillingResult.responseCode) {
        BillingClient.BillingResponseCode.OK -> {
          context!!.contentResolver?.applyBatch(
              AUTHORITY,
              ArrayList((skuDetailsList ?: emptyList<SkuDetails>()).map {
                ContentProviderOperation.newInsert(URI_PRODUCTS)
                    .withValues(ContentValues(6).apply {
                      put(COLUMN_KEY_SKU, it.sku)
                      put(COLUMN_KEY_TITLE, it.title)
                      put(COLUMN_KEY_TYPE, it.type)
                      put(COLUMN_KEY_PRICE, it.price)
                      put(COLUMN_KEY_PRICE_CURRENCY_CODE, it.priceCurrencyCode)
                      put(COLUMN_KEY_ORIGINAL_JSON, it.originalJson)
                    }).build()
              }))
        }
        else -> Handler(Looper.myLooper()!!).postDelayed({
          queryBillingClientForSkuDetails()
        }, 1000L)
      }
    } ?: Unit
  }

  override fun onBillingServiceDisconnected() {
    if (Looper.myLooper() != Looper.getMainLooper()) {
      Thread.sleep(1000)
    }
    try {
      billingClient?.startConnection(this) ?: Unit
    } catch (ignored: OutOfMemoryError) {
    }
  }

  override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
    when (billingResult?.responseCode) {
      BillingClient.BillingResponseCode.OK -> {
        if (!purchases.isNullOrEmpty()) {
          PreferenceManager.getDefaultSharedPreferences(context!!).edit()
              .putBoolean(context!!.getString(R.string.preference_key_show_alarm_banner), true)
              .apply()
        }
        purchases.orEmpty().onEach {
          if (!it.isAcknowledged) {
            billingClient?.acknowledgePurchase(AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(it.purchaseToken)
                .build(), this)
          }
        }
      }
      BillingClient.BillingResponseCode.USER_CANCELED -> Unit
      BillingClient.BillingResponseCode.ERROR,
      BillingClient.BillingResponseCode.SERVICE_DISCONNECTED,
      BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE ->
        Toast.makeText(context, R.string.error_billing_try_again_later, Toast.LENGTH_LONG)
            .show()
      BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED ->
        Toast.makeText(context, R.string.error_item_already_owned, Toast.LENGTH_LONG)
            .show()
      else -> {
        crashReporter.report(billingExceptionFromResponseCode(billingResult?.responseCode))
        Toast.makeText(context, R.string.error_billing_try_again_later, Toast.LENGTH_LONG)
            .show()
      }
    }
  }

  override fun onAcknowledgePurchaseResponse(billingResult: BillingResult) = Unit

  override fun insert(uri: Uri, contentValues: ContentValues?) = when (URI_MATCHER.match(uri)) {
    CODE_PRODUCT_LIST -> {
      dbHelper.writableDatabase.delete(TABLE_NAME_PRODUCTS, null, null)
      if (contentValues!!.getAsString(COLUMN_KEY_SKU).isNotEmpty()) {
        dbHelper.writableDatabase.insert(TABLE_NAME_PRODUCTS, null, contentValues)
      }
      context!!.contentResolver.notifyChange(uri, null)
      uri.buildUpon().appendPath(contentValues.getAsString(COLUMN_KEY_SKU)).build()
    }
    else -> {
      crashReporter.report(UnsupportedOperationException("IAP content provider got a call to insert for uri $uri. This never happens from inside the app. Possible tampering?"))
      null
    }
  }

  @SuppressLint("Recycle")
  override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, orderBy: String?): Cursor? = when (URI_MATCHER.match(uri)) {
    CODE_PRODUCT_PURCHASE -> {
      val activity = PurchaseManager.activityForPurchaseFlow.get()!!
      val cursor = query(URI_PRODUCTS, null, null, null, null)!!
      val json = cursor.let {
        generateSequence {
          if (it.moveToNext()) it else null
        }.firstOrNull()?.getString(it.getColumnIndex(COLUMN_KEY_ORIGINAL_JSON))
      }
      cursor.close()
      if (json == null) {
        Toast.makeText(context, R.string.error_billing_try_again_later, Toast.LENGTH_LONG)
            .show()
        null
      } else {
        billingClient?.launchBillingFlow(
            activity,
            BillingFlowParams.newBuilder()
                .setSkuDetails(SkuDetails(json))
                .build())
        MatrixCursor(projection)
      }
    }
    CODE_PRODUCT_LIST -> dbHelper.readableDatabase.query(TABLE_NAME_PRODUCTS, projection, selection, selectionArgs, null, null, orderBy)
    CODE_ACTIVE_PURCHASE_LIST ->
      Single.create<Cursor> { emitter ->
        val cursor = MatrixCursor(arrayOf(COLUMN_KEY_SKU, COLUMN_KEY_ORIGINAL_JSON))
        billingClient?.queryPurchases(BillingClient.SkuType.SUBS)?.apply {
          when (responseCode) {
            BillingClient.BillingResponseCode.OK -> {
              purchasesList?.forEach {
                if (System.currentTimeMillis() - it.purchaseTime > TimeUnit.DAYS.toMillis(7)) {
                  PurchaseManager.trialPeriodOver = true
                }
                cursor.addRow(arrayOf(it.sku, it.originalJson))
              }
            }
            else -> Unit
          }
        }
        emitter.onSuccess(cursor)
      }
          .subscribeOn(Schedulers.newThread())
          .blockingGet()
    else -> {
      crashReporter.report(UnsupportedOperationException("IAP content provider got a call to query for $uri. This never happens from inside the app. Possible tampering?"))
      null
    }
  }

  override fun update(p0: Uri, p1: ContentValues?, p2: String?, p3: Array<String>?): Int {
    crashReporter.report(UnsupportedOperationException("IAP content provider got a call to update. This never happens from inside the app. Possible tampering?"))
    return 0
  }

  override fun delete(uri: Uri, whereClause: String?, whereArgs: Array<String>?): Int {
    crashReporter.report(UnsupportedOperationException("IAP content provider got a call to delete This never happens from inside the app. Possible tampering?"))
    return 0
  }

  override fun getType(p0: Uri): String {
    crashReporter.report(UnsupportedOperationException("IAP content provider got a call to getType. This never happens from inside the app. Possible tampering?"))
    return ""
  }
}

private lateinit var AUTHORITY: String
internal lateinit var URI_PRODUCTS: Uri
internal lateinit var URI_ACTIVE_PURCHASES: Uri
lateinit var URI_COMMAND_PURCHASE: Uri
private val URI_MATCHER = UriMatcher(UriMatcher.NO_MATCH)
private const val CODE_ACTIVE_PURCHASE_LIST = 2
private const val CODE_PRODUCT = 3
private const val CODE_PRODUCT_LIST = 4
private const val CODE_PRODUCT_PURCHASE = 5
private const val PREFIX_CONTENT = "content://"
private const val DATA_TYPE_ACTIVE_PURCHASES = "active_purchases"
private const val DATA_TYPE_PRODUCTS = "products"
private const val COMMAND_PRODUCT_PURCHASE = "command_product_purchase"
