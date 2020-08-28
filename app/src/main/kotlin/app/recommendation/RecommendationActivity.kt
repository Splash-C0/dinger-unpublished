package app.recommendation

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.deviceOrientation
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import app.home.me.profile.birthDateToAgeString
import app.home.me.profile.milesToRoundedKms
import app.taskDescriptionFactory
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import domain.recommendation.DomainRecommendationUser
import domain.seen.SeenRecommendationViewModel
import kotlinx.android.synthetic.main.activity_recommendation.indicator
import kotlinx.android.synthetic.main.activity_recommendation.label_bio
import kotlinx.android.synthetic.main.activity_recommendation.label_distance
import kotlinx.android.synthetic.main.activity_recommendation.label_teaser
import kotlinx.android.synthetic.main.activity_recommendation.pager_pics
import kotlinx.android.synthetic.main.include_toolbar.toolbar
import org.stoyicker.dinger.R

internal class RecommendationActivity : AppCompatActivity(), OnViewForPhotoReady {
  private var model: DomainRecommendationUser? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_recommendation)
    setTaskDescription(taskDescriptionFactory(this))
    setSupportActionBar(toolbar)
    updateViewModelProvider()
  }

  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    this.intent = intent.apply {
      this?.extras
          ?.putString(
              EXTRA_KEY_RECOMMENDATION_ID,
              this@RecommendationActivity.intent.getStringExtra(EXTRA_KEY_RECOMMENDATION_ID)!!)
    }
    updateViewModelProvider()
  }

  private fun updateViewModelProvider() {
    ViewModelProvider(this)[SeenRecommendationViewModel::class.java]
        .get(intent?.getStringExtra(EXTRA_KEY_RECOMMENDATION_ID))?.observe(
            this, Observer { it?.let { updateUi(it) } })
  }

  private fun updateUi(model: DomainRecommendationUser) = with(model) {
    this@RecommendationActivity.model = this
    supportActionBar?.apply {
      setDisplayHomeAsUpEnabled(true)
      setHomeAsUpIndicator(R.drawable.ic_arrow_left)
      title = birthDate.run {
        if (this == null) {
          name
        } else {
          resources.getString(
              R.string.profile_title_pattern, name, birthDateToAgeString())
        }
      }
    }
    val teaser = teaser?.description
    if (teaser.isNullOrBlank()) {
      label_teaser.visibility = View.GONE
    } else {
      label_teaser.apply {
        visibility = View.VISIBLE
        text = teaser
      }
    }
    label_distance.text = getString(
        R.string.distance_label_pattern, distanceMiles.milesToRoundedKms())
    val bio = bio
    if (bio == null) {
      label_bio.visibility = View.GONE
    } else {
      label_bio.apply {
        visibility = View.VISIBLE
        text = bio
      }
    }
    val photos = photos.takeIf { it != null && it.count() > 0 }
    if (photos != null) {
      pager_pics.apply {
        visibility = View.VISIBLE
        adapter = RecommendationPagerAdapter(supportFragmentManager, photos.count()).apply {
          offscreenPageLimit = photos.count()
        }
        indicator.setViewPager(pager_pics)
      }
    } else {
      pager_pics.visibility = View.GONE
    }
  }

  override fun invoke(imageView: ImageView, index: Int) {
    val model = model
    if (model == null) {
      imageView.post {
        invoke(imageView, index)
      }
      return
    }
    val iterator = model.photos!!.iterator()
    repeat((0 until index).count()) { iterator.next() }
    val photo = iterator.next()
    Picasso.get()
        .load(photo.url)
        .fit()
        .run {
          if (windowManager.deviceOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
            centerInside()
          } else {
            centerCrop()
          }
        }
        .into(imageView, object : Callback {
          override fun onSuccess() = coverProgressDrawable()

          override fun onError(e: Exception?) {
            // Both are needed to make sure that the progress drawable is covered as required
            imageView.setImageDrawable(
                this@RecommendationActivity.getDrawable(R.drawable.ic_no_profile))
            coverProgressDrawable()
          }

          private fun coverProgressDrawable() {
            imageView.background = ColorDrawable(Color.BLACK)
          }
        })
  }
}

internal fun recommendationActivityCallingIntent(context: Context, recommendationId: String) =
    Intent(context, RecommendationActivity::class.java)
        .putExtra(EXTRA_KEY_RECOMMENDATION_ID, recommendationId)

private const val EXTRA_KEY_RECOMMENDATION_ID = "EXTRA_KEY_RECOMMENDATION_ID"
