package views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import org.stoyicker.dinger.views.R

class RoundCornerImageView(context: Context, attributeSet: AttributeSet? = null)
  : View(context, attributeSet), Target {
  private val cornerRadiusPx: Float
  private val picasso = Picasso.get()
  private val clipPath = Path()
  private var drawable: Drawable? = null
    set(value) {
      field = value
      postInvalidate()
    }
  private var queuedUrl: String? = null
  private var queuedErrorRes: Int = 0

  init {
    context.theme.obtainStyledAttributes(
        attributeSet,
        R.styleable.RoundCornerImageView,
        0,
        0).apply {
      try {
        cornerRadiusPx = getDimension(R.styleable.RoundCornerImageView_cornerRadius, DEFAULT_RADIUS_PX)
      } finally {
        recycle()
      }
    }
  }

  fun loadImage(url: String, @DrawableRes errorRes: Int = 0) {
    queuedUrl = url
    queuedErrorRes = errorRes
    loadImageInternal()
  }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    queuedUrl?.let { picasso.invalidate(it) }
    picasso.cancelRequest(this)
    clipPath.reset()
    RectF().apply {
      set(0f, 0f, w.toFloat(), h.toFloat())
      clipPath.addRoundRect(this, DEFAULT_RADIUS_PX, DEFAULT_RADIUS_PX, Path.Direction.CW)
    }
    clipPath.close()
    loadImageInternal(w, h)
  }

  override fun onDraw(canvas: Canvas) {
    val save = canvas.save()
    canvas.clipPath(clipPath)
    drawable?.apply {
      setBounds(0, 0, width, height)
      draw(canvas)
    }
    canvas.restoreToCount(save)
  }

  override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
    drawable = placeHolderDrawable
  }

  override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
    val roundedDrawable = RoundedBitmapDrawableFactory.create(resources, bitmap)
    layoutParams.apply {
      width = Math.min((parent as View).width, bitmap.width)
      height = bitmap.height
    }
    roundedDrawable.cornerRadius = cornerRadiusPx
    drawable = roundedDrawable
    layoutParams = layoutParams
  }

  override fun onBitmapFailed(e: Exception, errorDrawable: Drawable?) {
    layoutParams.apply {
      width = Math.min(width, height)
      height = width
    }
    drawable = errorDrawable
    layoutParams = layoutParams
  }

  private fun loadImageInternal(w: Int = Math.min((parent as View).width, width), h: Int = height) {
    if (w < 1 && h < 1) {
      return
    }
    queuedUrl?.let {
      picasso.load(it)
          .noPlaceholder()
          .centerCrop()
          .resize(w, h)
          .stableKey(it)
          .apply {
            if (queuedErrorRes != 0) {
              error(queuedErrorRes)
            }
          }
          .into(this)
    }
  }

  private companion object {
    const val DEFAULT_RADIUS_PX = 25f
  }
}
