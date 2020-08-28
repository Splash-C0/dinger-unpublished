package app.entryscreen.login

internal interface TinderLoginView {
  fun setRunning()

  fun setStale()

  fun setError(error: Throwable?)
}
