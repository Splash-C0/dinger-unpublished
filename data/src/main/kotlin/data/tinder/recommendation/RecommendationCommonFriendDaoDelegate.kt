package data.tinder.recommendation

import data.database.CollectibleDaoDelegate
import domain.recommendation.DomainRecommendationCommonFriend

internal class RecommendationCommonFriendDaoDelegate(
    private val commonFriendDao: RecommendationUserCommonFriendDao,
    private val userCommonFriendDelegate
    : RecommendationUser_RecommendationUserCommonFriendDao,
    private val photoDaoDelegate: CommonFriendPhotoDaoDelegate)
  : CollectibleDaoDelegate<String, DomainRecommendationCommonFriend>() {
  override fun selectByPrimaryKey(primaryKey: String) =
      commonFriendDao.selectCommonFriendById(primaryKey).firstOrNull()?.let {
        val photos = photoDaoDelegate.collectByPrimaryKeys(it.photos)
        it.recommendationUserCommonFriend.let {
          return DomainRecommendationCommonFriend(
              id = it.id,
              name = it.name,
              degree = it.degree,
              photos = photos)
        }
      } ?: DomainRecommendationCommonFriend.NONE

  override fun insertResolved(source: DomainRecommendationCommonFriend) {
    source.photos?.let { photoDaoDelegate.insertResolvedForCommonFriendId(source.id, it) }
    commonFriendDao.insertCommonFriend(
        RecommendationUserCommonFriendEntity(
            id = source.id,
            name = source.name,
            degree = source.degree))
  }

  fun insertResolvedForUserId(
      userId: String, commonFriends: Iterable<DomainRecommendationCommonFriend>) =
      commonFriends.forEach {
        insertResolved(it)
        userCommonFriendDelegate.insertUser_CommonFriend(
            RecommendationUserEntity_RecommendationUserCommonFriendEntity(
                recommendationUserEntityId = userId,
                recommendationUserCommonFriendEntityId = it.id))
      }
}
