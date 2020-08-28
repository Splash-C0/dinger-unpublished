package app.recommendation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import org.stoyicker.dinger.R

internal class RecommendationImageFragment : Fragment() {
  private val index: Int by lazy { arguments!!.getInt(EXTRA_KEY_INDEX) }
  private lateinit var onViewForPhotoReady: OnViewForPhotoReady

  override fun onAttach(context: Context) {
    super.onAttach(context)
    onViewForPhotoReady = context as OnViewForPhotoReady
  }

  override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
      inflater.inflate(R.layout.fragment_recommendation_image, container, false)!!

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    onViewForPhotoReady(view as ImageView, index)
  }
}

internal typealias OnViewForPhotoReady = (ImageView, Int) -> Unit

internal fun newRecommendationImageFragment(index: Int) = RecommendationImageFragment().apply {
  arguments = Bundle().apply {
    putInt(EXTRA_KEY_INDEX, index)
  }
}

private const val EXTRA_KEY_INDEX = "EXTRA_KEY_INDEX"
