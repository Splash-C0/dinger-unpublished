package app.entryscreen.alarmbanner

internal class ContinueCoordinator(
    private val resultCallback: ResultCallback,
    private val continueView: ContinueView) {
  fun enable() = continueView.clickListener { resultCallback.onContinueClicked() }

  interface ResultCallback {
    fun onContinueClicked()
  }
}
