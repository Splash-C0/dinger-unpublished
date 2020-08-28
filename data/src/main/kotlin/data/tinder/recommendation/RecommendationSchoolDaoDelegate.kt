package data.tinder.recommendation

import data.database.CollectibleDaoDelegate
import domain.recommendation.DomainRecommendationSchool

internal class RecommendationSchoolDaoDelegate(
    private val schoolDao: RecommendationUserSchoolDao,
    private val userSchoolDao: RecommendationUser_SchoolDao)
  : CollectibleDaoDelegate<String, DomainRecommendationSchool>() {
  override fun selectByPrimaryKey(primaryKey: String) =
      schoolDao.selectSchoolById(primaryKey).firstOrNull()?.let {
        return DomainRecommendationSchool(
            id = it.id,
            name = it.name)
      } ?: DomainRecommendationSchool.NONE

  override fun insertResolved(source: DomainRecommendationSchool) = schoolDao.insertSchool(
      RecommendationUserSchoolEntity(id = source.id, name = source.name))

  fun insertResolvedForUserId(userId: String, schools: Iterable<DomainRecommendationSchool>) =
      schools.forEach {
        insertResolved(it)
        userSchoolDao.insertUser_School(RecommendationUserEntity_RecommendationUserSchoolEntity(
            recommendationUserEntityId = userId,
            recommendationUserSchoolEntityName = it.name))
      }
}
