package data.tinder.recommendation

import data.database.CollectibleDaoDelegate
import domain.recommendation.DomainRecommendationCommonFriendPhoto

internal class CommonFriendPhotoDaoDelegate(
    private val photoDao: RecommendationUserCommonFriendPhotoDao,
    private val commonFriendPhotoDao: RecommendationUserCommonFriend_PhotoDao)
  : CollectibleDaoDelegate<String, DomainRecommendationCommonFriendPhoto>() {
  override fun insertResolved(source: DomainRecommendationCommonFriendPhoto) =
      photoDao.insertPhoto(RecommendationUserCommonFriendPhotoEntity(
          small = source.small, medium = source.medium, large = source.large))

  fun insertResolvedForCommonFriendId(
      commonFriendId: String,
      photos: Iterable<DomainRecommendationCommonFriendPhoto>) = photos.forEach {
    insertResolved(it)
    commonFriendPhotoDao.insertCommonFriend_Photo(
        RecommendationUserCommonFriendEntity_PhotoEntity(
            recommendationUserCommonFriendEntityId = commonFriendId,
            recommendationUserCommonFriendPhotoEntitySmall = it.small))
  }
}

