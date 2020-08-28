package tracker

abstract class EventTrackers {
  companion object {
    fun void(): EventTracker = EventTrackerImpl.Void
  }
}
