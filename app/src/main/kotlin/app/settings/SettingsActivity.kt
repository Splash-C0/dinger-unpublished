package app.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.taskDescriptionFactory
import kotlinx.android.synthetic.main.include_toolbar.toolbar
import org.stoyicker.dinger.R

internal class SettingsActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_settings)
    setTaskDescription(taskDescriptionFactory(this))
    setupToolbar()
    setupFragment(savedInstanceState)
  }

  private fun setupToolbar() {
    toolbar.setTitle(R.string.label_settings)
    setSupportActionBar(toolbar)
    supportActionBar?.apply {
      setDisplayHomeAsUpEnabled(true)
      setHomeAsUpIndicator(R.drawable.ic_arrow_left)
    }
  }

  @SuppressLint("CommitTransaction")
  private fun setupFragment(savedInstanceState: Bundle?) {
    if (savedInstanceState == null) {
      val fragment = supportFragmentManager
          .findFragmentByTag(SettingsPreferenceFragmentCompat.FRAGMENT_TAG)
          ?: SettingsPreferenceFragmentCompat()
      supportFragmentManager.beginTransaction().apply {
        replace(R.id.fragment_container,
            fragment, SettingsPreferenceFragmentCompat.FRAGMENT_TAG)
        commitNowAllowingStateLoss()
      }
    }
  }


  companion object {
    fun getCallingIntent(context: Context) = Intent(context, SettingsActivity::class.java)
  }
}
