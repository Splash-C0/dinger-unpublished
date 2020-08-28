package views

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.widget.AppCompatSeekBar

internal class AppCompat23SeekBar(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
  : AppCompatSeekBar(context, attrs, defStyleAttr) {
  private val shift: Int

  constructor(context: Context) : this(context, null, android.R.attr.seekBarStyle)

  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, android.R.attr.seekBarStyle)

  init {
    if (attrs != null) {
      with(context.obtainStyledAttributes(attrs, org.stoyicker.dinger.views.R.styleable.AppCompat23SeekBar, defStyleAttr, 0)) {
        shift = getInteger(org.stoyicker.dinger.views.R.styleable.AppCompat23SeekBar_min, 0)
        // Force refresh to apply the shift
        progress = progress
        max = max
        recycle()
      }
    } else {
      shift = 0
    }
  }

  override fun setProgress(progress: Int) {
    clearAnimation()
    val anim = ValueAnimator.ofInt(getProgress(), progress - shift)
    anim.duration = context.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
    anim.interpolator = AccelerateDecelerateInterpolator()
    anim.addUpdateListener { animation ->
      val animProgress = animation.animatedValue as Int
      super.setProgress(animProgress)
    }
    anim.start()
  }

  override fun setMax(max: Int) = super.setMax(max - shift)
}
