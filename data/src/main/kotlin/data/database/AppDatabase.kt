package data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import data.tinder.recommendation.RecommendationLikeDao
import data.tinder.recommendation.RecommendationLikeEntity
import data.tinder.recommendation.RecommendationPhoto_ProcessedFileDao
import data.tinder.recommendation.RecommendationProcessedFileDao
import data.tinder.recommendation.RecommendationSpotifyAlbum_ProcessedFileDao
import data.tinder.recommendation.RecommendationSpotifyArtistDao
import data.tinder.recommendation.RecommendationSpotifyThemeTrack_ArtistDao
import data.tinder.recommendation.RecommendationUserCommonFriendDao
import data.tinder.recommendation.RecommendationUserCommonFriendEntity
import data.tinder.recommendation.RecommendationUserCommonFriendEntity_PhotoEntity
import data.tinder.recommendation.RecommendationUserCommonFriendPhotoDao
import data.tinder.recommendation.RecommendationUserCommonFriendPhotoEntity
import data.tinder.recommendation.RecommendationUserCommonFriend_PhotoDao
import data.tinder.recommendation.RecommendationUserDao
import data.tinder.recommendation.RecommendationUserEntity
import data.tinder.recommendation.RecommendationUserEntity_RecommendationLikeEntity
import data.tinder.recommendation.RecommendationUserEntity_RecommendationUserCommonFriendEntity
import data.tinder.recommendation.RecommendationUserEntity_RecommendationUserJobEntity
import data.tinder.recommendation.RecommendationUserEntity_RecommendationUserPhotoEntity
import data.tinder.recommendation.RecommendationUserEntity_RecommendationUserSchoolEntity
import data.tinder.recommendation.RecommendationUserEntity_RecommendationUserTeaserEntity
import data.tinder.recommendation.RecommendationUserInstagramDao
import data.tinder.recommendation.RecommendationUserInstagramEntity
import data.tinder.recommendation.RecommendationUserInstagramEntity_RecommendationUserInstagramPhotoEntity
import data.tinder.recommendation.RecommendationUserInstagramPhotoDao
import data.tinder.recommendation.RecommendationUserInstagramPhotoEntity
import data.tinder.recommendation.RecommendationUserInstagram_InstagramPhotoDao
import data.tinder.recommendation.RecommendationUserJobDao
import data.tinder.recommendation.RecommendationUserJobEntity
import data.tinder.recommendation.RecommendationUserPhotoDao
import data.tinder.recommendation.RecommendationUserPhotoEntity
import data.tinder.recommendation.RecommendationUserPhotoEntity_RecommendationUserPhotoProcessedFileEntity
import data.tinder.recommendation.RecommendationUserPhotoProcessedFileEntity
import data.tinder.recommendation.RecommendationUserSchoolDao
import data.tinder.recommendation.RecommendationUserSchoolEntity
import data.tinder.recommendation.RecommendationUserSpotifyThemeTrackAlbumDao
import data.tinder.recommendation.RecommendationUserSpotifyThemeTrackAlbumEntity
import data.tinder.recommendation.RecommendationUserSpotifyThemeTrackAlbumEntity_RecommendationUserPhotoProcessedFileEntity
import data.tinder.recommendation.RecommendationUserSpotifyThemeTrackArtistEntity
import data.tinder.recommendation.RecommendationUserSpotifyThemeTrackDao
import data.tinder.recommendation.RecommendationUserSpotifyThemeTrackEntity
import data.tinder.recommendation.RecommendationUserSpotifyThemeTrackEntity_RecommendationUserSpotifyThemeTrackArtistEntity
import data.tinder.recommendation.RecommendationUserTeaserDao
import data.tinder.recommendation.RecommendationUserTeaserEntity
import data.tinder.recommendation.RecommendationUser_JobDao
import data.tinder.recommendation.RecommendationUser_LikeDao
import data.tinder.recommendation.RecommendationUser_PhotoDao
import data.tinder.recommendation.RecommendationUser_RecommendationUserCommonFriendDao
import data.tinder.recommendation.RecommendationUser_SchoolDao
import data.tinder.recommendation.RecommendationUser_TeaserDao

@Database(version = 6,
    entities = [
      // For RecommendationUserWithRelatives
      RecommendationUserEntity::class,
      RecommendationUserInstagramEntity::class,
      RecommendationUserInstagramPhotoEntity::class,
      RecommendationUserTeaserEntity::class,
      RecommendationUserSpotifyThemeTrackEntity::class,
      RecommendationUserSpotifyThemeTrackAlbumEntity::class,
      RecommendationUserEntity_RecommendationUserCommonFriendEntity::class,
      RecommendationUserEntity_RecommendationLikeEntity::class,
      RecommendationUserEntity_RecommendationUserPhotoEntity::class,
      RecommendationUserEntity_RecommendationUserJobEntity::class,
      RecommendationUserEntity_RecommendationUserSchoolEntity::class,
      RecommendationUserEntity_RecommendationUserTeaserEntity::class,
      // For RecommendationUserInstagramWithRelatives
      RecommendationUserInstagramEntity_RecommendationUserInstagramPhotoEntity::class,
      // For RecommendationUserPhotoWithRelatives
      RecommendationUserPhotoProcessedFileEntity::class,
      RecommendationUserPhotoEntity::class,
      RecommendationUserPhotoEntity_RecommendationUserPhotoProcessedFileEntity::class,
      // For RecommendationUserSpotifyThemeTrackWithRelatives
      RecommendationUserSpotifyThemeTrackArtistEntity::class,
      RecommendationUserSpotifyThemeTrackEntity_RecommendationUserSpotifyThemeTrackArtistEntity::class,
      // For RecommendationUserPhotoProcessedFileEntity
      RecommendationUserSpotifyThemeTrackAlbumEntity_RecommendationUserPhotoProcessedFileEntity::class,
      // For RecommendationUserCommonFriendEntity
      RecommendationUserCommonFriendEntity::class,
      RecommendationUserCommonFriendPhotoEntity::class,
      RecommendationUserCommonFriendEntity_PhotoEntity::class,
      // For RecommendationLikeEntity
      RecommendationLikeEntity::class,
      // For RecommendationUserJobEntity
      RecommendationUserJobEntity::class,
      // For RecommendationUserSchoolEntity
      RecommendationUserSchoolEntity::class])
internal abstract class AppDatabase : RoomDatabase() {
  abstract fun recommendationUserDao(): RecommendationUserDao

  abstract fun recommendationUserInstagramDao(): RecommendationUserInstagramDao

  abstract fun recommendationUserInstagramPhotoDao(): RecommendationUserInstagramPhotoDao

  abstract fun recommendationUserInstagram_PhotoDao(): RecommendationUserInstagram_InstagramPhotoDao

  abstract fun recommendationProcessedFileDao(): RecommendationProcessedFileDao

  abstract fun recommendationPhoto_ProcessedFileDao(): RecommendationPhoto_ProcessedFileDao

  abstract fun recommendationUserPhotoDao(): RecommendationUserPhotoDao

  abstract fun recommendationUserSpotifyThemeTrackDao(): RecommendationUserSpotifyThemeTrackDao

  abstract fun recommendationSpotifyArtistDao(): RecommendationSpotifyArtistDao

  abstract fun recommendationSpotifyThemeTrack_ArtistDao()
      : RecommendationSpotifyThemeTrack_ArtistDao

  abstract fun recommendationSpotifyAlbum_ProcessedFileDao()
      : RecommendationSpotifyAlbum_ProcessedFileDao

  abstract fun recommendationUserSpotifyThemeTrackAlbumDao()
      : RecommendationUserSpotifyThemeTrackAlbumDao

  abstract fun recommendationUserCommonFriendDao(): RecommendationUserCommonFriendDao

  abstract fun recommendationUser_RecommendationUserCommonFriendDao():
      RecommendationUser_RecommendationUserCommonFriendDao

  abstract fun recommendationUserCommonFriendPhotoDao()
      : RecommendationUserCommonFriendPhotoDao

  abstract fun recommendationUserCommonFriend_PhotoDao()
      : RecommendationUserCommonFriend_PhotoDao

  abstract fun recommendationLikeDao(): RecommendationLikeDao

  abstract fun recommendationUser_LikeDao(): RecommendationUser_LikeDao

  abstract fun recommendationUser_PhotoDao(): RecommendationUser_PhotoDao

  abstract fun recommendationUserJobDao(): RecommendationUserJobDao

  abstract fun recommendationUser_JobDao(): RecommendationUser_JobDao

  abstract fun recommendationUserSchoolDao(): RecommendationUserSchoolDao

  abstract fun recommendationUser_SchoolDao(): RecommendationUser_SchoolDao

  abstract fun recommendationUserTeaserDao(): RecommendationUserTeaserDao

  abstract fun recommendationUser_TeaserDao(): RecommendationUser_TeaserDao
}
