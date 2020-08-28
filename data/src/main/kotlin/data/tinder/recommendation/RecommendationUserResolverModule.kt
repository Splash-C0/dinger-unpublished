package data.tinder.recommendation

import dagger.Module
import dagger.Provides
import data.RootModule
import data.crash.CrashReporterModule
import data.database.AppDatabase
import reporter.CrashReporter
import javax.inject.Singleton

@Module(includes = [RootModule::class, CrashReporterModule::class])
internal class RecommendationUserResolverModule {
  @Provides
  @Singleton
  fun recommendationUserDao(appDatabase: AppDatabase) = appDatabase.recommendationUserDao()

  @Provides
  @Singleton
  fun commonFriendDao(appDatabase: AppDatabase) =
      appDatabase.recommendationUserCommonFriendDao()

  @Provides
  @Singleton
  fun user_CommonFriendDao(appDatabase: AppDatabase) =
      appDatabase.recommendationUser_RecommendationUserCommonFriendDao()

  @Provides
  @Singleton
  fun commonFriendPhotoDao(appDatabase: AppDatabase) =
      appDatabase.recommendationUserCommonFriendPhotoDao()

  @Provides
  @Singleton
  fun commonFriend_PhotoDao(appDatabase: AppDatabase) =
      appDatabase.recommendationUserCommonFriend_PhotoDao()

  @Provides
  @Singleton
  fun commonFriendPhotoDaoDelegate(
      photoDao: RecommendationUserCommonFriendPhotoDao,
      commonFriend_PhotoDao: RecommendationUserCommonFriend_PhotoDao) =
      CommonFriendPhotoDaoDelegate(photoDao, commonFriend_PhotoDao)

  @Provides
  fun instagramDao(appDatabase: AppDatabase) = appDatabase.recommendationUserInstagramDao()

  @Provides
  @Singleton
  fun instagramPhotoDao(appDatabase: AppDatabase) =
      appDatabase.recommendationUserInstagramPhotoDao()

  @Provides
  @Singleton
  fun instagram_PhotoDao(appDatabase: AppDatabase) =
      appDatabase.recommendationUserInstagram_PhotoDao()

  @Provides
  @Singleton
  fun instagramPhotoDaoDelegate(
      instagramPhotoDao: RecommendationUserInstagramPhotoDao,
      instagram_PhotoDao: RecommendationUserInstagram_InstagramPhotoDao) =
      RecommendationInstagramPhotoDaoDelegate(instagramPhotoDao, instagram_PhotoDao)

  @Provides
  @Singleton
  fun instagramDaoDelegate(
      instagramDao: RecommendationUserInstagramDao,
      instagramPhotoDaoDelegate: RecommendationInstagramPhotoDaoDelegate) =
      RecommendationInstagramDaoDelegate(instagramDao, instagramPhotoDaoDelegate)

  @Provides
  @Singleton
  fun commonFriendDaoDelegate(
      commonFriendDao: RecommendationUserCommonFriendDao,
      user_CommonFriendDao: RecommendationUser_RecommendationUserCommonFriendDao,
      commonFriendPhotoDaoDelegate: CommonFriendPhotoDaoDelegate) =
      RecommendationCommonFriendDaoDelegate(
          commonFriendDao,
          user_CommonFriendDao,
          commonFriendPhotoDaoDelegate)

  @Provides
  @Singleton
  fun likeDao(appDatabase: AppDatabase) = appDatabase.recommendationLikeDao()

  @Provides
  @Singleton
  fun user_LikeDao(appDatabase: AppDatabase) = appDatabase.recommendationUser_LikeDao()

  @Provides
  @Singleton
  fun likeDaoDelegate(
      likeDao: RecommendationLikeDao,
      user_LikeDao: RecommendationUser_LikeDao) =
      RecommendationLikeDaoDelegate(
          likeDao,
          user_LikeDao)

  @Provides
  @Singleton
  fun photoDao(appDatabase: AppDatabase) = appDatabase.recommendationUserPhotoDao()

  @Provides
  @Singleton
  fun processedFileDao(appDatabase: AppDatabase) = appDatabase.recommendationProcessedFileDao()

  @Provides
  @Singleton
  fun photo_ProcessedFileDao(appDatabase: AppDatabase) =
      appDatabase.recommendationPhoto_ProcessedFileDao()

  @Provides
  @Singleton
  fun album_ProcessedFileDao(appDatabase: AppDatabase) =
      appDatabase.recommendationSpotifyAlbum_ProcessedFileDao()

  @Provides
  @Singleton
  fun processedFileDaoDelegate(
      processedFileDao: RecommendationProcessedFileDao,
      photo_ProcessedFileDao: RecommendationPhoto_ProcessedFileDao,
      album_ProcessedFileDao: RecommendationSpotifyAlbum_ProcessedFileDao) =
      RecommendationProcessedFileDaoDelegate(
          processedFileDao,
          photo_ProcessedFileDao,
          album_ProcessedFileDao)

  @Provides
  @Singleton
  fun user_PhotoDao(appDatabase: AppDatabase) = appDatabase.recommendationUser_PhotoDao()

  @Provides
  @Singleton
  fun photoDaoDelegate(
      photoDao: RecommendationUserPhotoDao,
      processedFileDaoDelegate: RecommendationProcessedFileDaoDelegate,
      user_PhotoDao: RecommendationUser_PhotoDao) = RecommendationPhotoDaoDelegate(
      photoDao,
      user_PhotoDao,
      processedFileDaoDelegate)

  @Provides
  @Singleton
  fun jobDao(appDatabase: AppDatabase) = appDatabase.recommendationUserJobDao()

  @Provides
  @Singleton
  fun user_JobDao(appDatabase: AppDatabase) = appDatabase.recommendationUser_JobDao()

  @Provides
  @Singleton
  fun jobDaoDelegate(jobDao: RecommendationUserJobDao, user_JobDao: RecommendationUser_JobDao) =
      RecommendationJobDaoDelegate(
          jobDao,
          user_JobDao)

  @Provides
  @Singleton
  fun schoolDao(appDatabase: AppDatabase) = appDatabase.recommendationUserSchoolDao()

  @Provides
  @Singleton
  fun user_SchoolDao(appDatabase: AppDatabase) = appDatabase.recommendationUser_SchoolDao()

  @Provides
  @Singleton
  fun schoolDaoDelegate(
      schoolDao: RecommendationUserSchoolDao,
      user_SchoolDao: RecommendationUser_SchoolDao) = RecommendationSchoolDaoDelegate(
      schoolDao,
      user_SchoolDao)

  @Provides
  @Singleton
  fun teaserDao(appDatabase: AppDatabase) = appDatabase.recommendationUserTeaserDao()

  @Provides
  @Singleton
  fun user_teaserDao(appDatabase: AppDatabase) = appDatabase.recommendationUser_TeaserDao()

  @Provides
  @Singleton
  fun teaserDaoDelegate(
      teaserDao: RecommendationUserTeaserDao,
      user_TeaserDao: RecommendationUser_TeaserDao) = RecommendationTeaserDaoDelegate(
      teaserDao,
      user_TeaserDao)

  @Provides
  @Singleton
  fun spotifyThemeTrackDao(appDatabase: AppDatabase) =
      appDatabase.recommendationUserSpotifyThemeTrackDao()

  @Provides
  @Singleton
  fun spotifyArtistDao(appDatabase: AppDatabase) = appDatabase.recommendationSpotifyArtistDao()

  @Provides
  @Singleton
  fun spotifyArtist_TrackDao(appDatabase: AppDatabase) =
      appDatabase.recommendationSpotifyThemeTrack_ArtistDao()

  @Provides
  @Singleton
  fun spotifyArtistDaoDelegate(
      spotifyArtistDao: RecommendationSpotifyArtistDao,
      spotifyThemeTrack_ArtistDao: RecommendationSpotifyThemeTrack_ArtistDao) =
      RecommendationSpotifyArtistDaoDelegate(
          spotifyArtistDao,
          spotifyThemeTrack_ArtistDao)

  @Provides
  @Singleton
  fun spotifyAlbumDao(appDatabase: AppDatabase) =
      appDatabase.recommendationUserSpotifyThemeTrackAlbumDao()

  @Provides
  @Singleton
  fun spotifyAlbumDaoDelegate(
      spotifyAlbumDao: RecommendationUserSpotifyThemeTrackAlbumDao,
      processedFileDaoDelegate: RecommendationProcessedFileDaoDelegate) =
      RecommendationSpotifyAlbumDaoDelegate(
          spotifyAlbumDao,
          processedFileDaoDelegate)

  @Provides
  @Singleton
  fun spotifyThemeTrackDaoDelegate(
      spotifyThemeTrackDao: RecommendationUserSpotifyThemeTrackDao,
      spotifyAlbumDaoDelegate: RecommendationSpotifyAlbumDaoDelegate,
      spotifyArtistDaoDelegate: RecommendationSpotifyArtistDaoDelegate) =
      RecommendationSpotifyThemeTrackDaoDelegate(
          spotifyThemeTrackDao,
          spotifyArtistDaoDelegate,
          spotifyAlbumDaoDelegate)

  @Provides
  @Singleton
  fun recommendationUserResolver(
      userDao: RecommendationUserDao,
      commonFriendDaoDelegate: RecommendationCommonFriendDaoDelegate,
      instagramDaoDelegate: RecommendationInstagramDaoDelegate,
      likeDaoDelegate: RecommendationLikeDaoDelegate,
      photoDaoDelegate: RecommendationPhotoDaoDelegate,
      jobDaoDelegate: RecommendationJobDaoDelegate,
      schoolDaoDelegate: RecommendationSchoolDaoDelegate,
      teaserDaoDelegate: RecommendationTeaserDaoDelegate,
      spotifyThemeTrackDaoDelegate: RecommendationSpotifyThemeTrackDaoDelegate,
      crashReporter: CrashReporter) =
      RecommendationUserResolver(
          userDao,
          commonFriendDaoDelegate,
          instagramDaoDelegate,
          likeDaoDelegate,
          photoDaoDelegate,
          jobDaoDelegate,
          schoolDaoDelegate,
          teaserDaoDelegate,
          spotifyThemeTrackDaoDelegate,
          crashReporter)
}
