package data.tinder.recommendation

import android.database.SQLException
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.DataSource
import domain.recommendation.DomainRecommendationGender
import domain.recommendation.DomainRecommendationUser
import reporter.CrashReporter
import java.util.Calendar
import java.util.Date
import java.util.Locale

internal class RecommendationUserResolver(
    private val userDao: RecommendationUserDao,
    private val commonFriendDaoDelegate: RecommendationCommonFriendDaoDelegate,
    private val instagramDaoDelegate: RecommendationInstagramDaoDelegate,
    private val likeDaoDelegate: RecommendationLikeDaoDelegate,
    private val photoDaoDelegate: RecommendationPhotoDaoDelegate,
    private val jobDaoDelegate: RecommendationJobDaoDelegate,
    private val schoolDaoDelegate: RecommendationSchoolDaoDelegate,
    private val teaserDaoDelegate: RecommendationTeaserDaoDelegate,
    private val spotifyThemeTrackDaoDelegate: RecommendationSpotifyThemeTrackDaoDelegate,
    private val crashReporter: CrashReporter) {
  fun insert(user: DomainRecommendationUser) =
      try {
        user.apply {
          instagramDaoDelegate.insertResolved(instagram)
          teaser?.let { teaserDaoDelegate.insertResolved(it) }
          spotifyThemeTrackDaoDelegate.insertResolved(spotifyThemeTrack)
          commonFriendDaoDelegate.insertResolvedForUserId(id, commonFriends)
          likeDaoDelegate.insertResolvedForUserId(id, commonLikes)
          photoDaoDelegate.insertResolvedForUserId(id, photos ?: emptyList())
          jobDaoDelegate.insertResolvedForUserId(id, jobs)
          schoolDaoDelegate.insertResolvedForUserId(id, schools)
          teaserDaoDelegate.insertResolvedForUserId(id, teasers)
          userDao.insertUser(RecommendationUserEntity(
              bio = bio,
              distanceMiles = distanceMiles,
              commonFriendCount = commonFriendCount,
              commonLikeCount = commonLikeCount,
              contentHash = contentHash,
              id = id,
              birthDate = birthDate,
              name = name,
              instagram = instagram?.username,
              teaser = teaser?.id,
              sNumber = sNumber,
              spotifyThemeTrack = spotifyThemeTrack?.id,
              gender = gender.ordinal,
              birthDateInfo = birthDateInfo,
              groupMatched = groupMatched,
              liked = liked,
              matched = matched,
              insertionEpoch = System.currentTimeMillis(),
              filterableContent = "${name.toLowerCase(Locale.getDefault())}/${bio?.toLowerCase(Locale.getDefault()) ?: ""}/${birthDate.birthDateToAgeString().toLowerCase(Locale.getDefault())}/${instagram?.username?.toLowerCase(Locale.getDefault()) ?: ""}/${teaser?.description?.toLowerCase(Locale.getDefault()) ?: ""}"
          ))
        }
      } catch (sqlException: SQLException) {
        crashReporter.report(sqlException)
      }

  fun selectById(id: String): LiveData<DomainRecommendationUser> =
      Transformations.map(userDao.selectUserById(id)) { from(it.first()) }

  fun selectByFilter(filter: String)
      : DataSource.Factory<Int, DomainRecommendationUser> =
      userDao.selectUsersByFilter(filter.toLowerCase(Locale.getDefault())).map { input -> from(input) }

  fun selectMatchedByFilterOnName(filter: String)
      : DataSource.Factory<Int, DomainRecommendationUser> =
      userDao.selectMatchedUsersByFilterOnName(filter.toLowerCase(Locale.getDefault())).map { from(it) }

  private fun from(source: RecommendationUserWithRelatives): DomainRecommendationUser {
    val commonFriends =
        commonFriendDaoDelegate.collectByPrimaryKeys(source.commonFriends)
    val commonLikes = likeDaoDelegate.collectByPrimaryKeys(source.commonLikes)
    val photos = photoDaoDelegate.collectByPrimaryKeys(source.photos)
    val jobs = jobDaoDelegate.collectByPrimaryKeys(source.jobs)
    val schools = schoolDaoDelegate.collectByPrimaryKeys(source.schools)
    val teasers = teaserDaoDelegate.collectByPrimaryKeys(source.teasers)
    source.recommendationUserEntity.let {
      return DomainRecommendationUser(
          bio = it.bio,
          distanceMiles = it.distanceMiles,
          commonFriends = commonFriends,
          commonFriendCount = it.commonFriendCount,
          commonLikes = commonLikes,
          commonLikeCount = it.commonLikeCount,
          contentHash = it.contentHash,
          id = it.id,
          birthDate = it.birthDate,
          name = it.name,
          instagram = instagramDaoDelegate.selectByPrimaryKey(it.instagram),
          teaser = teaserDaoDelegate.selectByPrimaryKey(it.teaser),
          sNumber = it.sNumber,
          spotifyThemeTrack =
          spotifyThemeTrackDaoDelegate.selectByPrimaryKey(it.spotifyThemeTrack),
          gender = DomainRecommendationGender.fromGenderInt(it.gender),
          birthDateInfo = it.birthDateInfo,
          groupMatched = it.groupMatched,
          liked = it.liked,
          matched = it.matched,
          photos = photos,
          jobs = jobs,
          schools = schools,
          teasers = teasers)
    }
  }
}

private fun Date?.birthDateToAgeString(): String {
  if (this == null) {
    return ""
  }
  val a = asCalendar()
  val b = Date().asCalendar()
  var diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR)
  if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) || a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE)) {
    diff--
  }
  return "$diff"
}

private fun Date.asCalendar() = Calendar.getInstance(Locale.US).apply {
  time = this@asCalendar
}
