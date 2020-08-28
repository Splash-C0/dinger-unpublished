package data.tinder

import data.tinder.like.LikeRatingRequest
import data.tinder.login.facebook.FacebookLoginRequestParameters
import data.tinder.login.sms.SmsLoginRequestParameters
import data.tinder.login.sms.SmsRequestOneTimePasswordRequestParameters
import data.tinder.login.sms.SmsVerifyOneTimePasswordRequestParameters
import io.reactivex.Single
import okhttp3.ResponseBody
import org.stoyicker.dinger.data.BuildConfig
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

internal interface TinderApi {
  @POST("/v2/auth/login/facebook")
  fun getTinderApiTokenViaFacebook(@Body requestParameters: FacebookLoginRequestParameters): Single<ResponseBody>

  @POST("/v2/auth/sms/send?auth_type=sms")
  fun requestOneTimePassword(
      @Body requestOneTimePasswordRequestParameters: SmsRequestOneTimePasswordRequestParameters): Single<ResponseBody>

  @POST("/v2/auth/sms/validate?auth_type=sms")
  fun getTinderApiTokenViaSmsVerifyOneTimePassword(
      @Body verifyOneTimePasswordRequestParameters: SmsVerifyOneTimePasswordRequestParameters): Single<ResponseBody>

  @POST("/v2/auth/login/sms")
  fun getTinderApiTokenViaSms(@Body requestParameters: SmsLoginRequestParameters): Single<ResponseBody>

  @GET("/recs/core?locale=${BuildConfig.TINDER_API_LOCALE}")
  fun getRecommendations(): Single<ResponseBody>

  @POST("/like/{targetId}")
  fun like(@Path("targetId") targetId: String, @Body likeRatingRequest: LikeRatingRequest): Single<ResponseBody>

  @GET("/pass/{targetId}")
  fun dislike(@Path("targetId") targetId: String): Single<ResponseBody>

  @GET("/profile")
  fun getProfile(): Single<ResponseBody>

  companion object {
    const val BASE_URL = "https://api.gotinder.com"
    const val CONTENT_TYPE_JSON = "application/json"
    const val HEADER_AUTH = "X-Auth-Token"
    const val HEADER_CONTENT_TYPE = "Content-Type"
    const val HEADER_PLATFORM = "platform"
  }
}
