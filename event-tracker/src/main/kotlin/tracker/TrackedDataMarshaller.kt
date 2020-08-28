package tracker

import android.os.Bundle

interface TrackedDataMarshaller<in T> {
  fun marshall(source: T): Bundle
}
