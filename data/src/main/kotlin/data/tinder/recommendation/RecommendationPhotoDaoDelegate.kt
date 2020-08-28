package data.tinder.recommendation

import data.database.CollectibleDaoDelegate
import domain.recommendation.DomainRecommendationPhoto

internal class RecommendationPhotoDaoDelegate(
    private val photoDao: RecommendationUserPhotoDao,
    private val userPhotoDao: RecommendationUser_PhotoDao,
    private val processedFileDaoDelegate: RecommendationProcessedFileDaoDelegate)
  : CollectibleDaoDelegate<String, DomainRecommendationPhoto>() {
  override fun selectByPrimaryKey(primaryKey: String) =
      photoDao.selectPhotoById(primaryKey).firstOrNull()?.let {
        val processedFiles =
            processedFileDaoDelegate.collectByPrimaryKeys(it.processedFiles)
        it.recommendationUserPhotoEntity.let {
          return DomainRecommendationPhoto(
              id = it.id,
              url = it.url,
              processedFiles = processedFiles)
        }
      } ?: DomainRecommendationPhoto.NONE

  override fun insertResolved(source: DomainRecommendationPhoto) {
    processedFileDaoDelegate.insertResolvedForPhotoId(source.id, source.processedFiles)
    photoDao.insertPhoto(RecommendationUserPhotoEntity(id = source.id, url = source.url))
  }

  fun insertResolvedForUserId(userId: String, photos: Iterable<DomainRecommendationPhoto>) =
      photos.forEach {
        insertResolved(it)
        userPhotoDao.insertUser_Photo(RecommendationUserEntity_RecommendationUserPhotoEntity(
            recommendationUserEntityId = userId,
            recommendationUserPhotoEntityId = it.id))
      }
}
