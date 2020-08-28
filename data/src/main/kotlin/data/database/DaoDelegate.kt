package data.database

internal abstract class DaoDelegate<in PrimaryKey, Domain> {
  open fun selectByPrimaryKey(primaryKey: PrimaryKey): Domain =
      throw NotImplementedError("Not implemented.")

  abstract fun insertResolved(source: Domain)
}
