package data.tinder.recommendation

import data.ObjectMapper
import domain.recommendation.DomainRecommendationCommonFriend
import domain.recommendation.DomainRecommendationCommonFriendPhoto
import domain.recommendation.DomainRecommendationGender
import domain.recommendation.DomainRecommendationInstagram
import domain.recommendation.DomainRecommendationInstagramPhoto
import domain.recommendation.DomainRecommendationJob
import domain.recommendation.DomainRecommendationJobCompany
import domain.recommendation.DomainRecommendationJobTitle
import domain.recommendation.DomainRecommendationLike
import domain.recommendation.DomainRecommendationPhoto
import domain.recommendation.DomainRecommendationProcessedFile
import domain.recommendation.DomainRecommendationSchool
import domain.recommendation.DomainRecommendationSpotifyAlbum
import domain.recommendation.DomainRecommendationSpotifyArtist
import domain.recommendation.DomainRecommendationSpotifyThemeTrack
import domain.recommendation.DomainRecommendationTeaser
import domain.recommendation.DomainRecommendationUser
import reporter.CrashReporter

internal class RecommendationResponseObjectMapper(
    crashReporter: CrashReporter,
    private val eventTracker: RecommendationEventTracker,
    private val commonFriendDelegate
    : ObjectMapper<RecommendationUserCommonFriend, DomainRecommendationCommonFriend>,
    private val instagramDelegate
    : ObjectMapper<RecommendationUserInstagram, DomainRecommendationInstagram>,
    private val teaserDelegate
    : ObjectMapper<RecommendationUserTeaser, DomainRecommendationTeaser>,
    private val spotifyThemeTrackDelegate
    : ObjectMapper<RecommendationUserSpotifyThemeTrack, DomainRecommendationSpotifyThemeTrack>,
    private val commonLikeDelegate
    : ObjectMapper<RecommendationLike, DomainRecommendationLike>,
    private val photoDelegate
    : ObjectMapper<RecommendationUserPhoto, DomainRecommendationPhoto>,
    private val jobDelegate: ObjectMapper<RecommendationUserJob, DomainRecommendationJob>,
    private val schoolDelegate: ObjectMapper<RecommendationUserSchool,
        DomainRecommendationSchool>)
  : ObjectMapper<RecommendationResponse, List<DomainRecommendationUser>>(crashReporter) {
  override fun fromImpl(source: RecommendationResponse): List<DomainRecommendationUser> =
      source.let {
        eventTracker.track(it)
        return it.recommendations.let {
          when (it) {
            null -> throw when (source.message) {
              is String -> Error(source.message)
              else -> IllegalStateException(
                  "Unexpected 2xx (${source.status}) recommendation response without message." +
                      "Array size: ${source.recommendations?.size ?: 0}")
            }
            else -> it.map { transformRecommendation(it) }
          }
        }
      }


  private fun transformRecommendation(source: Recommendation) = DomainRecommendationUser(
      bio = source.bio,
      distanceMiles = source.distanceMiles,
      commonFriendCount = source.commonFriendCount,
      commonFriends = source.commonFriends.mapNotNull {
        commonFriendDelegate.from(it)
      },
      commonLikeCount = source.commonLikeCount,
      commonLikes = source.commonLikes.mapNotNull {
        commonLikeDelegate.from(it)
      },
      id = source.id,
      birthDate = source.birthDate,
      name = source.name,
      instagram = instagramDelegate.from(source.instagram),
      teaser = teaserDelegate.from(source.teaser),
      spotifyThemeTrack = spotifyThemeTrackDelegate.from(source.spotifyThemeTrack),
      gender = DomainRecommendationGender.fromGenderInt(source.gender),
      birthDateInfo = source.birthDateInfo,
      contentHash = source.contentHash,
      groupMatched = source.groupMatched,
      sNumber = source.sNumber,
      photos = source.photos.mapNotNull { photoDelegate.from(it) },
      jobs = source.jobs.mapNotNull { jobDelegate.from(it) },
      schools = source.schools.mapNotNull { schoolDelegate.from(it) },
      teasers = source.teasers.mapNotNull { teaserDelegate.from(it) })
}

internal class RecommendationUserCommonFriendObjectMapper(
    crashReporter: CrashReporter,
    private val commonFriendPhotoDelegate
    : ObjectMapper<RecommendationUserCommonFriendPhoto, DomainRecommendationCommonFriendPhoto>)
  : ObjectMapper<RecommendationUserCommonFriend, DomainRecommendationCommonFriend>(crashReporter) {
  override fun fromImpl(source: RecommendationUserCommonFriend) =
      DomainRecommendationCommonFriend(
          id = source.id,
          name = source.name,
          degree = source.degree,
          photos = source.photos?.mapNotNull { commonFriendPhotoDelegate.from(it) })
}

internal class RecommendationUserCommonFriendPhotoObjectMapper(crashReporter: CrashReporter)
  : ObjectMapper<RecommendationUserCommonFriendPhoto, DomainRecommendationCommonFriendPhoto>(
    crashReporter) {
  override fun fromImpl(source: RecommendationUserCommonFriendPhoto) =
      DomainRecommendationCommonFriendPhoto(
          small = source.small,
          medium = source.medium,
          large = source.large)
}

internal class RecommendationInstagramObjectMapper(
    crashReporter: CrashReporter,
    private val instagramPhotoDelegate
    : ObjectMapper<RecommendationUserInstagramPhoto, DomainRecommendationInstagramPhoto>)
  : ObjectMapper<RecommendationUserInstagram, DomainRecommendationInstagram>(crashReporter) {
  override fun fromImpl(source: RecommendationUserInstagram) =
      DomainRecommendationInstagram(
          profilePictureUrl = source.profilePictureUrl,
          lastFetchTime = source.lastFetchTime,
          mediaCount = source.mediaCount,
          completedInitialFetch = source.completedInitialFetch,
          username = source.username,
          photos = source.photos?.mapNotNull { instagramPhotoDelegate.from(it) })
}

internal class RecommendationInstagramPhotoObjectMapper(crashReporter: CrashReporter)
  : ObjectMapper<RecommendationUserInstagramPhoto, DomainRecommendationInstagramPhoto>(
    crashReporter) {
  override fun fromImpl(source: RecommendationUserInstagramPhoto) =
      DomainRecommendationInstagramPhoto(
          link = source.link,
          imageUrl = source.imageUrl,
          thumbnailUrl = source.thumbnailUrl,
          ts = source.ts)
}

internal class RecommendationTeaserObjectMapper(crashReporter: CrashReporter)
  : ObjectMapper<RecommendationUserTeaser, DomainRecommendationTeaser>(crashReporter) {
  override fun fromImpl(source: RecommendationUserTeaser) =
      DomainRecommendationTeaser(
          id = RecommendationUserTeaserEntity.createId(
              description = source.description,
              type = source.type),
          description = source.description,
          type = source.type)
}

internal class RecommendationSpotifyThemeTrackObjectMapper(
    crashReporter: CrashReporter,
    private val albumDelegate
    : ObjectMapper<RecommendationUserSpotifyThemeTrackAlbum, DomainRecommendationSpotifyAlbum>,
    private val artistDelegate
    : ObjectMapper<RecommendationUserSpotifyThemeTrackArtist, DomainRecommendationSpotifyArtist>)
  : ObjectMapper<RecommendationUserSpotifyThemeTrack, DomainRecommendationSpotifyThemeTrack>(
    crashReporter) {
  override fun fromImpl(source: RecommendationUserSpotifyThemeTrack) =
      DomainRecommendationSpotifyThemeTrack(
          album = albumDelegate.from(source.album),
          artists = source.artists.mapNotNull { artistDelegate.from(it) },
          id = source.id,
          name = source.name,
          previewUrl = source.previewUrl,
          uri = source.uri)
}

internal class RecommendationSpotifyThemeTrackAlbumObjectMapper(
    crashReporter: CrashReporter,
    private val processedFileDelegate
    : ObjectMapper<RecommendationUserPhotoProcessedFile, DomainRecommendationProcessedFile>)
  : ObjectMapper<RecommendationUserSpotifyThemeTrackAlbum, DomainRecommendationSpotifyAlbum>(crashReporter) {
  override fun fromImpl(source: RecommendationUserSpotifyThemeTrackAlbum) =
      DomainRecommendationSpotifyAlbum(
          id = source.id,
          name = source.name,
          images = source.images?.mapNotNull { processedFileDelegate.from(it) } ?: emptySet())
}

internal class RecommendationProcessedFileObjectMapper(crashReporter: CrashReporter)
  : ObjectMapper<RecommendationUserPhotoProcessedFile, DomainRecommendationProcessedFile>(crashReporter) {
  override fun fromImpl(source: RecommendationUserPhotoProcessedFile) =
      DomainRecommendationProcessedFile(
          widthPx = source.widthPx,
          url = source.url,
          heightPx = source.heightPx)
}

internal class RecommendationSpotifyThemeTrackArtistObjectMapper(crashReporter: CrashReporter)
  : ObjectMapper<RecommendationUserSpotifyThemeTrackArtist, DomainRecommendationSpotifyArtist>(
    crashReporter) {
  override fun fromImpl(source: RecommendationUserSpotifyThemeTrackArtist) =
      DomainRecommendationSpotifyArtist(id = source.id, name = source.name)
}

internal class RecommendationLikeObjectMapper(crashReporter: CrashReporter)
  : ObjectMapper<RecommendationLike, DomainRecommendationLike>(crashReporter) {
  override fun fromImpl(source: RecommendationLike) =
      DomainRecommendationLike(id = source.id, name = source.name)
}

internal class RecommendationPhotoObjectMapper(
    crashReporter: CrashReporter,
    private val processedFileDelegate
    : ObjectMapper<RecommendationUserPhotoProcessedFile, DomainRecommendationProcessedFile>)
  : ObjectMapper<RecommendationUserPhoto, DomainRecommendationPhoto>(crashReporter) {
  override fun fromImpl(source: RecommendationUserPhoto) = DomainRecommendationPhoto(
      id = source.id,
      url = source.url,
      processedFiles = source.processedFiles.mapNotNull { processedFileDelegate.from(it) })
}

internal class RecommendationJobObjectMapper(
    crashReporter: CrashReporter,
    private val companyDelegate: ObjectMapper<RecommendationUserJobCompany, DomainRecommendationJobCompany>,
    private val titleDelegate: ObjectMapper<RecommendationUserJobTitle, DomainRecommendationJobTitle>)
  : ObjectMapper<RecommendationUserJob, DomainRecommendationJob>(crashReporter) {
  override fun fromImpl(source: RecommendationUserJob) = DomainRecommendationJob(
      id = RecommendationUserJob.createId(source.company, source.title),
      company = source.company?.let { companyDelegate.from(it) },
      title = source.title?.let { titleDelegate.from(it) })
}

internal class RecommendationJobCompanyObjectMapper(crashReporter: CrashReporter)
  : ObjectMapper<RecommendationUserJobCompany, DomainRecommendationJobCompany>(crashReporter) {
  override fun fromImpl(source: RecommendationUserJobCompany) = DomainRecommendationJobCompany(
      name = source.name
  )
}

internal class RecommendationJobTitleObjectMapper(crashReporter: CrashReporter)
  : ObjectMapper<RecommendationUserJobTitle, DomainRecommendationJobTitle>(crashReporter) {
  override fun fromImpl(source: RecommendationUserJobTitle) = DomainRecommendationJobTitle(
      name = source.name
  )
}

internal class RecommendationSchoolObjectMapper(crashReporter: CrashReporter)
  : ObjectMapper<RecommendationUserSchool, DomainRecommendationSchool>(crashReporter) {
  override fun fromImpl(source: RecommendationUserSchool) = DomainRecommendationSchool(
      id = source.id,
      name = source.name)
}
