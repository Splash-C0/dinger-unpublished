package data.network

import io.reactivex.Single

/**
 * Support for offline requests.
 */
internal interface Gettable<in RequestModel, ResponseModel>
  : Fetchable<RequestModel, ResponseModel> {
  /**
   * Implement this method to execute your request.
   * @param parameters The parameters for the request.
   */
  fun get(parameters: RequestModel): Single<ResponseModel>
}
