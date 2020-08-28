package app.entryscreen.alarmbanner

import android.view.View

internal class ContinueViewImpl(private val view: View) : ContinueView {
  override fun clickListener(function: () -> Unit) {
    view.setOnClickListener { function() }
  }
}
