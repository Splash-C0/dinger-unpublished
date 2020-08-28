package data.tinder.recommendation

import data.database.CollectibleDaoDelegate
import domain.recommendation.DomainRecommendationTeaser

internal class RecommendationTeaserDaoDelegate(
    private val teaserDao: RecommendationUserTeaserDao,
    private val userTeaserDao: RecommendationUser_TeaserDao)
  : CollectibleDaoDelegate<String?, DomainRecommendationTeaser>() {
  override fun selectByPrimaryKey(primaryKey: String?) =
      teaserDao.selectTeaserById(primaryKey).firstOrNull()?.let {
        return@let DomainRecommendationTeaser(
            id = it.id,
            description = it.description,
            type = it.type)
      } ?: DomainRecommendationTeaser.NONE

  override fun insertResolved(source: DomainRecommendationTeaser) = teaserDao
      .insertTeaser(RecommendationUserTeaserEntity(
          id = source.id,
          description = source.description,
          type = source.type))

  fun insertResolvedForUserId(userId: String, teasers: Iterable<DomainRecommendationTeaser>) =
      teasers.forEach {
        insertResolved(it)
        userTeaserDao.insertUser_Teaser(RecommendationUserEntity_RecommendationUserTeaserEntity(
            recommendationUserEntityId = userId,
            recommendationUserTeaserEntityId = it.id))
      }
}
