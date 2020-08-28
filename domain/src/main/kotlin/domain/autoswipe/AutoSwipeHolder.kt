package domain.autoswipe

object AutoSwipeHolder {
  lateinit var autoSwipeLauncherFactory: AutoSwipeLauncherFactory

  fun autoSwipeIntentFactory(
      autoSwipeLauncherFactory: AutoSwipeLauncherFactory) {
    this.autoSwipeLauncherFactory = autoSwipeLauncherFactory
  }
}
