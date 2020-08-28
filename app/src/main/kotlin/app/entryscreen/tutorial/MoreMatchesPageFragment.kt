package app.entryscreen.tutorial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_more_matches.next
import org.stoyicker.dinger.R

internal class MoreMatchesPageFragment : Fragment() {
  override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
      inflater.inflate(R.layout.fragment_more_matches, container, false)!!

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) =
      next.setOnClickListener { (activity as TutorialInteractions).onNextRequested() }

  companion object {
    @JvmStatic
    fun newInstance() = MoreMatchesPageFragment()
  }
}
