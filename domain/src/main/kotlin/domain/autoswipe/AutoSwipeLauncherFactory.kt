package domain.autoswipe

import android.content.Context
import android.content.Intent

interface AutoSwipeLauncherFactory {
  fun newFromBroadcast(context: Context): Intent
}
