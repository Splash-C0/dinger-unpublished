package iaps

import android.content.Context
import android.database.sqlite.SQLiteDatabase

internal class IAPsSQLiteDao(context: Context)
  : RobustSQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
  override fun onCreate(db: SQLiteDatabase) {
    super.onCreate(db)
    val createProductsTableCmd = "CREATE TABLE IF NOT EXISTS $TABLE_NAME_PRODUCTS( " +
        "$COLUMN_KEY_SKU TEXT PRIMARY KEY ON CONFLICT REPLACE, " +
        "$COLUMN_KEY_TITLE TEXT NOT NULL ON CONFLICT REPLACE, " +
        "$COLUMN_KEY_TYPE TEXT NOT NULL ON CONFLICT REPLACE, " +
        "$COLUMN_KEY_PRICE NUMERIC NOT NULL ON CONFLICT REPLACE, " +
        "$COLUMN_KEY_PRICE_CURRENCY_CODE TEXT NOT NULL ON CONFLICT REPLACE, " +
        "$COLUMN_KEY_ORIGINAL_JSON TEXT NOT NULL ON CONFLICT REPLACE)"
    db.execSQL(createProductsTableCmd)
    val createActivePurchasesTableCmd = "CREATE TABLE IF NOT EXISTS $TABLE_NAME_ACTIVE_PURCHASES( " +
        "$COLUMN_KEY_ORDER_ID TEXT PRIMARY KEY ON CONFLICT REPLACE, " +
        "$COLUMN_KEY_SKU TEXT UNIQUE ON CONFLICT REPLACE NOT NULL ON CONFLICT REPLACE, " +
        "$COLUMN_KEY_PURCHASE_TOKEN TEXT UNIQUE NOT NULL ON CONFLICT REPLACE, " +
        "$COLUMN_KEY_PURCHASE_EPOCH INTEGER UNIQUE NOT NULL ON CONFLICT REPLACE, " +
        "$COLUMN_KEY_ORIGINAL_JSON TEXT NOT NULL ON CONFLICT REPLACE)"
    db.execSQL(createActivePurchasesTableCmd)
  }

  override fun onRobustUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    // No upgrades so far
  }
}

private const val DATABASE_VERSION = 1
private const val DATABASE_NAME = "billing"
internal const val TABLE_NAME_PRODUCTS = "PRODUCTS"
internal const val COLUMN_KEY_SKU = "SKU"
internal const val COLUMN_KEY_TITLE = "TITLE"
internal const val COLUMN_KEY_TYPE = "TYPE"
internal const val COLUMN_KEY_PRICE = "PRICE"
internal const val COLUMN_KEY_PRICE_CURRENCY_CODE = "CURRENCY_CODE"
internal const val COLUMN_KEY_ORIGINAL_JSON = "ORIGINAL_JSON"
internal const val TABLE_NAME_ACTIVE_PURCHASES = "ACTIVE_PURCHASES"
internal const val COLUMN_KEY_ORDER_ID = "ORDER_ID"
internal const val COLUMN_KEY_PURCHASE_TOKEN = "PURCHASE_TOKEN"
internal const val COLUMN_KEY_PURCHASE_EPOCH = "PURCHASE_EPOCH"
