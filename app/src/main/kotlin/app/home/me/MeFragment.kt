package app.home.me

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import app.home.me.profile.GetProfileCoordinator
import app.home.me.profile.ProfileView
import org.stoyicker.dinger.R
import javax.inject.Inject

internal class MeFragment : Fragment() {
  @Inject
  lateinit var contentView: ProfileView
  @Inject
  lateinit var getProfileCoordinator: GetProfileCoordinator

  override fun onCreateView(inflater: LayoutInflater,
                            container: ViewGroup?,
                            savedInstanceState: Bundle?): View =
      inflater.inflate(R.layout.fragment_me, container, false)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    inject(view)
    contentView.setup()
    getProfileCoordinator.actionGet()
  }

  private fun inject(view: View) = DaggerMeComponent.factory()
      .create(view = view)
      .inject(this)

  override fun onDestroyView() {
    getProfileCoordinator.actionCancel()
    super.onDestroyView()
  }

  companion object {
    fun newInstance() = MeFragment()
  }
}
