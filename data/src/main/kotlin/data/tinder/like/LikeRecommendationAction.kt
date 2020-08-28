package data.tinder.like

import data.autoswipe.AutoSwipeIntentService
import domain.interactor.DisposableUseCase
import domain.like.DomainLikedRecommendationAnswer
import domain.like.LikeRecommendationUseCase
import domain.recommendation.DomainRecommendationUser
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

internal class LikeRecommendationAction(private val user: DomainRecommendationUser)
  : AutoSwipeIntentService.Action<LikeRecommendationAction.Callback>() {
  private var useCaseDelegate: DisposableUseCase? = null

  override fun execute(owner: AutoSwipeIntentService, callback: Callback) =
      LikeRecommendationUseCase(user, Schedulers.trampoline()).let {
        useCaseDelegate = it
        it.execute(object : DisposableSingleObserver<DomainLikedRecommendationAnswer>() {
          override fun onSuccess(payload: DomainLikedRecommendationAnswer) {
            callback.onRecommendationLiked(payload)
          }

          override fun onError(error: Throwable) {
            callback.onRecommendationLikeFailed(error)
          }
        })
      }

  override fun dispose() {
    useCaseDelegate?.dispose()
  }

  interface Callback {
    fun onRecommendationLiked(answer: DomainLikedRecommendationAnswer)

    fun onRecommendationLikeFailed(error: Throwable)
  }
}
