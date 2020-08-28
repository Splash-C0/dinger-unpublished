package data.database

import android.database.sqlite.SQLiteDatabaseLockedException

internal abstract class CollectibleDaoDelegate<in PrimaryKey, Resolved>
  : DaoDelegate<PrimaryKey, Resolved>() {
  fun collectByPrimaryKeys(primaryKeys: Iterable<PrimaryKey>): Iterable<Resolved> =
      primaryKeys.fold(emptySet()) { acc, s ->
        try {
          selectByPrimaryKey(s)
        } catch (ignored: SQLiteDatabaseLockedException) {
          null
        }?.let { acc.plus(it) } ?: acc
      }
}
