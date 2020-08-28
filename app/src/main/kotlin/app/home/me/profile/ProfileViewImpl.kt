package app.home.me.profile

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.ContentLoadingProgressBar
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import domain.profile.DomainGetProfileAnswer
import org.stoyicker.dinger.R
import java.util.Calendar
import java.util.Calendar.DATE
import java.util.Calendar.MONTH
import java.util.Calendar.YEAR
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

internal class ProfileViewImpl(
    private val profileImage: ImageView,
    private val titleLabel: TextView,
    private val occupationLabel: TextView,
    private val locationLabel: TextView,
    private val bioLabel: TextView,
    private val distanceFilterLabel: TextView,
    private val distanceFilter: ProgressBar,
    private val ageFilterLabel: TextView,
    private val ageFilterMin: ProgressBar,
    private val ageFilterMax: ProgressBar,
    private val progress: ContentLoadingProgressBar) : ProfileView {
  private val content = arrayOf(
      profileImage,
      titleLabel,
      occupationLabel,
      locationLabel,
      bioLabel,
      distanceFilterLabel,
      distanceFilter,
      ageFilterLabel,
      ageFilterMin,
      ageFilterMax)

  @SuppressLint("ClickableViewAccessibility")
  override fun setup() = content.forEach { it.setOnTouchListener(ON_TOUCH_LISTENER_DISALLOW) }

  override fun setData(data: DomainGetProfileAnswer) {
    profileImage.apply {
      when (val it = data.photos?.firstOrNull()) {
        null -> visibility = View.GONE
        else -> {
          Picasso.get()
              .load(it.url)
              .fit()
              .centerCrop()
              .transform(RoundedTransformationBuilder()
                  .borderColor(ContextCompat.getColor(context, R.color.text_primary))
                  .borderWidthDp(2f)
                  .cornerRadiusDp(1000F) // Needed for some reason, else we get a square
                  .oval(false)
                  .build())
              .into(this)
        }
      }
    }
    titleLabel.text = data.birthDate.run {
      if (this == null) {
        data.name
      } else {
        titleLabel.context.resources.getString(
            R.string.profile_title_pattern, data.name, birthDateToAgeString())
      }
    }
    occupationLabel.apply {
      if (data.jobs?.any() ?: data.schools?.any() != null) {
        visibility = View.VISIBLE
        text = data.jobs?.firstOrNull()?.title?.name ?: data.schools?.firstOrNull()?.name
      } else {
        visibility = View.GONE
      }
    }
    locationLabel.apply {
      if (data.positionInfo != null) {
        visibility = View.VISIBLE
        text = data.positionInfo!!.city?.name ?: data.positionInfo!!.country?.name
      } else {
        visibility = View.GONE
      }
    }
    bioLabel.text = data.bio
    distanceFilterLabel.text = distanceFilterLabel.context.getString(
        R.string.distance_filter_label_pattern, (data.distanceFilter ?: 0).milesToRoundedKms())
    distanceFilter.progress = (data.distanceFilter ?: 0).milesToRoundedKms()
    ageFilterLabel.text = ageFilterLabel.context.getString(
        R.string.age_filter_label_pattern,
        data.ageFilterMin.toString(),
        (data.ageFilterMax ?: 0).run {
          if (this > ageFilterLabel.context.resources.getInteger(R.integer.age_filter_max))
            ageFilterLabel.context.resources.getString(R.string.age_filter_max_infinite)
          else
            this.toString()
        })
    ageFilterMin.progress = data.ageFilterMin ?: 0
    ageFilterMax.progress = data.ageFilterMax ?: 0
    content.forEach { it.visibility = View.VISIBLE }
    progress.hide()
  }

  override fun setProgress() {
    progress.show()
    content.forEach { it.visibility = View.GONE }
  }

  override fun setError() {
    progress.hide()
    content.forEach { it.visibility = View.GONE }
  }
}

internal fun Date?.birthDateToAgeString(): String {
  if (this == null) {
    return ""
  }
  val a = asCalendar()
  val b = Date().asCalendar()
  var diff = b.get(YEAR) - a.get(YEAR)
  if (a.get(MONTH) > b.get(MONTH) || a.get(MONTH) == b.get(MONTH) && a.get(DATE) > b.get(DATE)) {
    diff--
  }
  return "$diff"
}

internal fun Int.milesToRoundedKms() = (this * MILE_TO_KMS_RATIO).roundToInt()

private fun Date.asCalendar() = Calendar.getInstance(Locale.US).apply {
  time = this@asCalendar
}

private val ON_TOUCH_LISTENER_DISALLOW = { _: View?, _: MotionEvent? -> true }
private const val MILE_TO_KMS_RATIO = 1.609344 // This is the amount of kms in a mile
