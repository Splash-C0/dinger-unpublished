package data.autoswipe

import android.content.Context
import android.content.SharedPreferences
import data.tinder.dislike.DislikeRecommendationAction
import data.tinder.dislike.DislikeRecommendationActionFactoryWrapper
import data.tinder.like.LikeRecommendationAction
import data.tinder.like.LikeRecommendationActionFactoryWrapper
import domain.dislike.DomainDislikedRecommendationAnswer
import domain.like.DomainLikedRecommendationAnswer
import domain.recommendation.DomainRecommendationUser
import org.stoyicker.dinger.data.R

internal class ProcessRecommendationAction(
    private val context: Context,
    private val user: DomainRecommendationUser,
    private val sharedPreferences: SharedPreferences,
    private val likeRecommendationActionFactory
    : dagger.Lazy<LikeRecommendationActionFactoryWrapper>,
    private val dislikeRecommendationActionFactory
    : dagger.Lazy<DislikeRecommendationActionFactoryWrapper>)
  : AutoSwipeIntentService.Action<ProcessRecommendationAction.Callback>() {
  private var runningAction: AutoSwipeIntentService.Action<*>? = null

  override fun execute(owner: AutoSwipeIntentService, callback: Callback) {
    when {
      sharedPreferences.getBoolean(
          context.getString(
              R.string.preference_key_dislike_empty_profiles), false) && user.bio.isNullOrBlank() ->
        dislikeRecommendation(owner, callback)
      sharedPreferences.getBoolean(
          context.getString(
              R.string.preference_key_dislike_if_friends_in_common), false) &&
          user.commonFriends.any() ->
        dislikeRecommendation(owner, callback)
      else -> likeRecommendation(owner, callback)
    }
  }


  override fun dispose() {
    runningAction?.dispose()
  }

  private fun dislikeRecommendation(owner: AutoSwipeIntentService, callback: Callback) =
      dislikeRecommendationActionFactory.get().delegate(user).let {
        runningAction = it
        it.execute(
            owner, object : DislikeRecommendationAction.Callback {
          override fun onRecommendationDisliked(
              answer: DomainDislikedRecommendationAnswer) {
            callback.onRecommendationProcessed(DomainLikedRecommendationAnswer.EMPTY, false)
          }

          override fun onRecommendationDislikeFailed() {
            callback.onRecommendationProcessingFailed()
          }
        })
      }

  private fun likeRecommendation(owner: AutoSwipeIntentService, callback: Callback) =
      likeRecommendationActionFactory.get().delegate(user).let {
        runningAction = it
        it.execute(
            owner, object : LikeRecommendationAction.Callback {
          override fun onRecommendationLiked(answer: DomainLikedRecommendationAnswer) {
            commonDelegate.onComplete(owner)
            callback.onRecommendationProcessed(answer, answer.rateLimitedUntilMillis == null)
          }

          override fun onRecommendationLikeFailed(error: Throwable) {
            commonDelegate.onError(error, owner)
            callback.onRecommendationProcessingFailed()
          }
        })
      }

  interface Callback {
    fun onRecommendationProcessed(answer: DomainLikedRecommendationAnswer, liked: Boolean)

    fun onRecommendationProcessingFailed()
  }
}
