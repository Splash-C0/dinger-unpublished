package data.tinder.recommendation

import data.database.CollectibleDaoDelegate
import domain.recommendation.DomainRecommendationLike

internal class RecommendationLikeDaoDelegate(
    private val likeDao: RecommendationLikeDao,
    private val userLikeDao: RecommendationUser_LikeDao)
  : CollectibleDaoDelegate<String, DomainRecommendationLike>() {
  override fun selectByPrimaryKey(primaryKey: String) =
      likeDao.selectLikeById(primaryKey).firstOrNull()?.let {
        return@let DomainRecommendationLike(
            id = it.id,
            name = it.name)
      } ?: DomainRecommendationLike.NONE

  override fun insertResolved(source: DomainRecommendationLike) =
      likeDao.insertLike(RecommendationLikeEntity(
          id = source.id,
          name = source.name))

  fun insertResolvedForUserId(
      userId: String, likes: Iterable<DomainRecommendationLike>) = likes.forEach {
    insertResolved(it)
    userLikeDao.insertUser_Like(RecommendationUserEntity_RecommendationLikeEntity(
        recommendationUserEntityId = userId,
        recommendationLikeEntityId = it.id))
  }
}
