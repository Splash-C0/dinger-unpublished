package app.home.seen.filter

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Filterable
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.text.HtmlCompat
import org.stoyicker.dinger.R

/**
 * Contains boilerplate for list filtering.
 */
class FilterDelegate(
    private val searchView: SearchView,
    private val target: Filterable) {
  internal var query: CharSequence = ""
    private set

  fun init(activity: Activity) {
    searchView.apply {
      setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?) = false

        override fun onQueryTextChange(newText: String?): Boolean {
          this@FilterDelegate.query = newText ?: ""
          target.filter.filter(query)
          return true
        }
      })
      findViewById<View>(androidx.appcompat.R.id.search_close_btn)?.setOnClickListener {
        setQuery("", false)
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(applicationWindowToken, 0)
        clearFocus()
      }
      findViewById<View>(androidx.appcompat.R.id.search_plate)?.background = ColorDrawable(Color.TRANSPARENT)
      setSearchableInfo((context.getSystemService(Context.SEARCH_SERVICE) as SearchManager)
          .getSearchableInfo(activity.componentName))
      setIconifiedByDefault(false)
      findViewById<EditText>(androidx.appcompat.R.id.search_src_text)?.setHintTextColor(Color.parseColor("#707070"))
      queryHint = HtmlCompat.fromHtml(context.getString(R.string.hint_search), 0)
    }
    activity.setDefaultKeyMode(AppCompatActivity.DEFAULT_KEYS_SEARCH_LOCAL)
  }

  /**
   * Delegates a query to the query handler in order to filter the list.
   * @param newQuery The query.
   */
  fun applyQuery(newQuery: CharSequence?) = searchView.setQuery(newQuery, false)
}
