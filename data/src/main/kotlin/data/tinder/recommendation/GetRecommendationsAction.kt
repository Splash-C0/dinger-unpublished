package data.tinder.recommendation

import data.autoswipe.AutoSwipeIntentService
import domain.interactor.DisposableUseCase
import domain.recommendation.DomainRecommendationUser
import domain.recommendation.GetRecommendationsUseCase
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

internal class GetRecommendationsAction
  : AutoSwipeIntentService.Action<GetRecommendationsAction.Callback>() {
  private var useCaseDelegate: DisposableUseCase? = null

  override fun execute(owner: AutoSwipeIntentService, callback: Callback) =
      GetRecommendationsUseCase(Schedulers.trampoline()).let {
        useCaseDelegate = it
        it.execute(object
          : DisposableSingleObserver<List<DomainRecommendationUser>>() {
          override fun onSuccess(payload: List<DomainRecommendationUser>) {
            commonDelegate.onComplete(owner)
            callback.onRecommendationsReceived(payload)
          }

          override fun onError(error: Throwable) = commonDelegate.onError(error, owner)
        })
      }

  override fun dispose() {
    useCaseDelegate?.dispose()
  }

  interface Callback {
    fun onRecommendationsReceived(recommendations: List<DomainRecommendationUser>)
  }
}
