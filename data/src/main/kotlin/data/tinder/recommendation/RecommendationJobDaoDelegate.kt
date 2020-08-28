package data.tinder.recommendation

import data.database.CollectibleDaoDelegate
import domain.recommendation.DomainRecommendationJob
import domain.recommendation.DomainRecommendationJobCompany
import domain.recommendation.DomainRecommendationJobTitle

internal class RecommendationJobDaoDelegate(
    private val jobDao: RecommendationUserJobDao,
    private val userJobDao: RecommendationUser_JobDao)
  : CollectibleDaoDelegate<String, DomainRecommendationJob>() {
  override fun selectByPrimaryKey(primaryKey: String) =
      jobDao.selectJobById(primaryKey).firstOrNull()?.let {
        return@let DomainRecommendationJob(
            id = it.id,
            company = it.company?.let { DomainRecommendationJobCompany(it.name) },
            title = it.title?.let { DomainRecommendationJobTitle(it.name) })
      } ?: DomainRecommendationJob.NONE

  override fun insertResolved(source: DomainRecommendationJob) = jobDao.insertJob(
      RecommendationUserJobEntity(
          id = source.id,
          company = source.company?.let { RecommendationUserJobCompany(it.name) },
          title = source.title?.let { RecommendationUserJobTitle(it.name) }))

  fun insertResolvedForUserId(userId: String, jobs: Iterable<DomainRecommendationJob>) =
      jobs.forEach {
        insertResolved(it)
        userJobDao.insertUser_Job(RecommendationUserEntity_RecommendationUserJobEntity(
            recommendationUserEntityId = userId,
            recommendationUserJobEntityId = it.id))
      }
}
