package app.home.me.profile

import domain.profile.DomainGetProfileAnswer

internal interface ProfileView {
  fun setup()

  fun setProgress()

  fun setData(data: DomainGetProfileAnswer)

  fun setError()
}
