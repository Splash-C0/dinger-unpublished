package data.network

import data.ObjectMapper
import io.reactivex.Single

internal abstract class RequestFacade<
    in RequestModel : Any, MappedRequestModel : Any, ResponseModel : Any, MappedModel : Any>(
    internal val source: RequestSource<MappedRequestModel, ResponseModel>,
    private val requestMapper: ObjectMapper<RequestModel, MappedRequestModel>,
    private val responseMapper: ObjectMapper<ResponseModel, MappedModel>)
  : Fetchable<RequestModel, MappedModel> {
  /** Request parameter mappers should return Unit instead of null, so !! should be safe here
   * If a request mapper ever returns null, this will cause a KotlinNPE and definitely needs to
   * be looked into
   */
  override fun fetch(parameters: RequestModel) = map(source.fetch(requestMapper.from(parameters)!!))

  /**
   * Maps items on the given series to another model.
   * @param sourceStream The series whose items are to be mapped.
   */
  fun map(sourceStream: Single<ResponseModel>): Single<MappedModel> =
      sourceStream.map { responseMapper.from(it) }
}
