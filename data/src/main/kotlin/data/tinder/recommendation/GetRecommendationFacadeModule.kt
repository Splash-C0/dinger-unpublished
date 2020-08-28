package data.tinder.recommendation

import dagger.Module
import dagger.Provides
import data.crash.CrashReporterModule
import reporter.CrashReporter
import javax.inject.Singleton

@Module(includes = [
  CrashReporterModule::class,
  RecommendationSourceModule::class,
  RecommendationEventTrackerModule::class])
internal class GetRecommendationFacadeModule {
  @Provides
  @Singleton
  fun requestObjectMapper(crashReporter: CrashReporter) =
      RecommendationRequestObjectMapper(crashReporter)

  @Provides
  @Singleton
  fun commonFriendPhotoObjectMapper(crashReporter: CrashReporter) =
      RecommendationUserCommonFriendPhotoObjectMapper(crashReporter)

  @Provides
  @Singleton
  fun commonFriendObjectMapper(
      crashReporter: CrashReporter,
      photoDelegate: RecommendationUserCommonFriendPhotoObjectMapper) =
      RecommendationUserCommonFriendObjectMapper(crashReporter, photoDelegate)

  @Provides
  @Singleton
  fun instagramPhotoObjectMapper(crashReporter: CrashReporter) =
      RecommendationInstagramPhotoObjectMapper(crashReporter)

  @Provides
  @Singleton
  fun instagramObjectMapper(
      crashReporter: CrashReporter,
      instagramPhotoDelegate: RecommendationInstagramPhotoObjectMapper) =
      RecommendationInstagramObjectMapper(crashReporter, instagramPhotoDelegate)

  @Provides
  @Singleton
  fun teaserObjectMapper(crashReporter: CrashReporter) =
      RecommendationTeaserObjectMapper(crashReporter)

  @Provides
  @Singleton
  fun processedFileObjectMapper(crashReporter: CrashReporter) =
      RecommendationProcessedFileObjectMapper(crashReporter)

  @Provides
  @Singleton
  fun spotifyAlbumObjectMapper(
      crashReporter: CrashReporter,
      processedFileDelegate: RecommendationProcessedFileObjectMapper) =
      RecommendationSpotifyThemeTrackAlbumObjectMapper(crashReporter, processedFileDelegate)

  @Provides
  @Singleton
  fun spotifyArtistObjectMapper(crashReporter: CrashReporter) =
      RecommendationSpotifyThemeTrackArtistObjectMapper(crashReporter)

  @Provides
  @Singleton
  fun spotifyThemeTrackObjectMapper(
      crashReporter: CrashReporter,
      albumDelegate: RecommendationSpotifyThemeTrackAlbumObjectMapper,
      artistDelegate: RecommendationSpotifyThemeTrackArtistObjectMapper) =
      RecommendationSpotifyThemeTrackObjectMapper(
          crashReporter,
          albumDelegate = albumDelegate,
          artistDelegate = artistDelegate)

  @Provides
  @Singleton
  fun likeObjectMapper(crashReporter: CrashReporter) = RecommendationLikeObjectMapper(crashReporter)

  @Provides
  @Singleton
  fun photoObjectMapper(
      crashReporter: CrashReporter,
      processedFileDelegate: RecommendationProcessedFileObjectMapper) =
      RecommendationPhotoObjectMapper(crashReporter, processedFileDelegate)

  @Provides
  @Singleton
  fun jobCompanyObjectMapper(crashReporter: CrashReporter) =
      RecommendationJobCompanyObjectMapper(crashReporter)

  @Provides
  @Singleton
  fun jobTitleObjectMapper(crashReporter: CrashReporter) =
      RecommendationJobTitleObjectMapper(crashReporter)

  @Provides
  @Singleton
  fun jobObjectMapper(
      crashReporter: CrashReporter,
      companyDelegate: RecommendationJobCompanyObjectMapper,
      titleDelegate: RecommendationJobTitleObjectMapper) =
      RecommendationJobObjectMapper(
          crashReporter,
          companyDelegate = companyDelegate,
          titleDelegate = titleDelegate)

  @Provides
  @Singleton
  fun schoolObjectMapper(crashReporter: CrashReporter) =
      RecommendationSchoolObjectMapper(crashReporter)

  @Provides
  @Singleton
  fun responseObjectMapper(
      crashReporter: CrashReporter,
      eventTracker: RecommendationEventTracker,
      commonFriendDelegate: RecommendationUserCommonFriendObjectMapper,
      instagramDelegate: RecommendationInstagramObjectMapper,
      teaserDelegate: RecommendationTeaserObjectMapper,
      spotifyThemeTrackDelegate: RecommendationSpotifyThemeTrackObjectMapper,
      commonLikeDelegate: RecommendationLikeObjectMapper,
      photoDelegate: RecommendationPhotoObjectMapper,
      jobDelegate: RecommendationJobObjectMapper,
      schoolDelegate: RecommendationSchoolObjectMapper) =
      RecommendationResponseObjectMapper(
          crashReporter,
          eventTracker = eventTracker,
          commonFriendDelegate = commonFriendDelegate,
          instagramDelegate = instagramDelegate,
          teaserDelegate = teaserDelegate,
          spotifyThemeTrackDelegate = spotifyThemeTrackDelegate,
          commonLikeDelegate = commonLikeDelegate,
          photoDelegate = photoDelegate,
          jobDelegate = jobDelegate,
          schoolDelegate = schoolDelegate)

  @Provides
  @Singleton
  fun facade(
      getRecommendationSource: GetRecommendationSource,
      requestObjectMapper: RecommendationRequestObjectMapper,
      responseObjectMapper: RecommendationResponseObjectMapper) =
      GetRecommendationFacade(getRecommendationSource, requestObjectMapper, responseObjectMapper)
}
