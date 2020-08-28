package data.network

import io.reactivex.Single

/**
 * Support for network requests.
 */
internal interface Fetchable<in RequestModel, ResponseModel> {
  /**
   * Implement this method to execute your request.
   * @param parameters The parameters for the request.
   */
  fun fetch(parameters: RequestModel): Single<ResponseModel>
}
