package data.network

import com.nytimes.android.external.store3.base.impl.Store
import io.reactivex.Single
import reporter.CrashReporter

internal abstract class RequestSource<in RequestModel, ResponseModel>(
    private val store: Store<ResponseModel, RequestModel>,
    private val crashReporter: CrashReporter)
  : Gettable<RequestModel, ResponseModel> {
  override fun fetch(parameters: RequestModel): Single<ResponseModel> =
      // noinspection CheckResult - False positive
      operate(store.fetch(parameters).onErrorResumeNext { error ->
        get(parameters).onErrorResumeNext(Single.error(error))
      })

  override fun get(parameters: RequestModel): Single<ResponseModel> =
      operate(store.get(parameters))

  /**
   * Do whatever operations you want on the data at this level.
   * @param sourceStream The stream whose items are to be mapped.
   */
  open fun operate(sourceStream: Single<ResponseModel>) = sourceStream
}
