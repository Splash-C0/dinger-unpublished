package iaps

/*
 * ConnectBot: simple, powerful, open-source SSH client for Android
 * Copyright 2007 Kenny Root, Jeffrey Sharkey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import java.util.LinkedList

abstract class RobustSQLiteOpenHelper(context: Context,
                                      name: String?,
                                      factory: CursorFactory?,
                                      version: Int)
  : SQLiteOpenHelper(context, name, factory, version) {

  override fun onCreate(db: SQLiteDatabase) {
    dropAllTables(db)
  }

  final override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    try {
      onRobustUpgrade(db, oldVersion, newVersion)
    } catch (e: SQLiteException) {
      // The database has entered an unknown state. Try to recover.
      try {
        regenerateTables(db)
      } catch (e2: SQLiteException) {
        dropAndCreateTables(db)
      }

    }

  }

  @Throws(SQLiteException::class)
  abstract fun onRobustUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int)

  private fun regenerateTables(db: SQLiteDatabase) {
    dropAllTablesWithPrefix(db, "OLD_")

    for (tableName in mTableNames)
      db.execSQL("ALTER TABLE " + tableName + " RENAME TO OLD_"
          + tableName)

    onCreate(db)

    for (tableName in mTableNames)
      repopulateTable(db, tableName)

    dropAllTablesWithPrefix(db, "OLD_")
  }

  private fun repopulateTable(db: SQLiteDatabase, tableName: String) {
    val columns = getTableColumnNames(db, tableName)

    val sql = "INSERT INTO $tableName ($columns) SELECT $columns FROM OLD_$tableName"
    db.execSQL(sql)
  }

  private fun getTableColumnNames(db: SQLiteDatabase, tableName: String): String {
    val sb = StringBuilder()

    val fields = db.rawQuery("PRAGMA table_info($tableName)", null)
    while (fields.moveToNext()) {
      if (!fields.isFirst)
        sb.append(", ")
      sb.append(fields.getString(1))
    }
    fields.close()

    return sb.toString()
  }

  private fun dropAndCreateTables(db: SQLiteDatabase) {
    dropAllTables(db)
    onCreate(db)
  }

  private fun dropAllTablesWithPrefix(db: SQLiteDatabase, prefix: String) {
    val deletedTables = LinkedList<String>()
    for (tableName in mTableNames) {
      db.execSQL("DROP TABLE IF EXISTS $prefix$tableName")
      deletedTables.add(prefix + tableName)
    }
    for (tableName in deletedTables)
      mTableNames.remove(tableName)
  }

  private fun dropAllTables(db: SQLiteDatabase) {
    dropAllTablesWithPrefix(db, "")
  }

  companion object {
    private val mTableNames = LinkedList<String>()

    protected fun addTableName(tableName: String) {
      mTableNames.add(tableName)
    }

    protected fun removeTableName(tableName: String) {
      mTableNames.remove(tableName)
    }
  }
}
