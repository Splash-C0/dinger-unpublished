package data.tinder.profile

import dagger.Module
import dagger.Provides
import data.crash.CrashReporterModule
import reporter.CrashReporter
import javax.inject.Singleton

@Module(includes = [CrashReporterModule::class, GetProfileSourceModule::class])
internal class GetProfileFacadeModule {
  @Provides
  @Singleton
  fun requestObjectMapper(crashReporter: CrashReporter) =
      GetProfileRequestObjectMapper(crashReporter)

  @Provides
  @Singleton
  fun profileEmailSettingsObjectMapper(crashReporter: CrashReporter) =
      ProfileEmailSettingsMapper(crashReporter)

  @Provides
  @Singleton
  fun profileJobTitleMapper(crashReporter: CrashReporter) =
      ProfileJobTitleMapper(crashReporter)

  @Provides
  @Singleton
  fun profileJobMapper(crashReporter: CrashReporter, jobTitleMapper: ProfileJobTitleMapper) =
      ProfileJobMapper(crashReporter, jobTitleMapper)

  @Provides
  @Singleton
  fun profileProcessedPhotoObjectMapper(crashReporter: CrashReporter) =
      ProfileProcessedPhotoObjectMapper(crashReporter)

  @Provides
  @Singleton
  fun profilePhotoObjectMapper(
      crashReporter: CrashReporter,
      processedFileMapper: ProfileProcessedPhotoObjectMapper) =
      ProfilePhotoObjectMapper(crashReporter, processedFileMapper)

  @Provides
  @Singleton
  fun profilePositionObjectMapper(crashReporter: CrashReporter) =
      ProfilePositionObjectMapper(crashReporter)

  @Provides
  @Singleton
  fun profileCoordinateMapper(crashReporter: CrashReporter) = ProfileCoordinateMapper(crashReporter)

  @Provides
  @Singleton
  fun profileBoundObjectMapper(
      crashReporter: CrashReporter,
      profileCoordinateMapper: ProfileCoordinateMapper) =
      ProfileBoundObjectMapper(crashReporter, profileCoordinateMapper)

  @Provides
  @Singleton
  fun profileCityObjectMapper(
      crashReporter: CrashReporter,
      profileBoundObjectMapper: ProfileBoundObjectMapper) =
      ProfileCityObjectMapper(crashReporter, profileBoundObjectMapper)

  @Provides
  @Singleton
  fun profileCountryObjectMapper(
      crashReporter: CrashReporter,
      profileBoundObjectMapper: ProfileBoundObjectMapper) =
      ProfileCountryObjectMapper(crashReporter, profileBoundObjectMapper)

  @Provides
  @Singleton
  fun profilePositionInfoObjectMapper(
      crashReporter: CrashReporter,
      cityMapper: ProfileCityObjectMapper,
      countryMapper: ProfileCountryObjectMapper) =
      ProfilePositionInfoObjectMapper(crashReporter, cityMapper, countryMapper)

  @Provides
  @Singleton
  fun profileSchoolObjectMapper(crashReporter: CrashReporter) =
      ProfileSchoolObjectMapper(crashReporter)

  @Provides
  @Singleton
  fun responseObjectMapper(
      crashReporter: CrashReporter,
      profileEmailSettingsMapper: ProfileEmailSettingsMapper,
      profileJobMapper: ProfileJobMapper,
      profilePhotoObjectMapper: ProfilePhotoObjectMapper,
      profilePositionObjectMapper: ProfilePositionObjectMapper,
      profilePositionInfoObjectMapper: ProfilePositionInfoObjectMapper,
      profileSchoolObjectMapper: ProfileSchoolObjectMapper) =
      GetProfileResponseObjectMapper(
          crashReporter,
          profileEmailSettingsMapper,
          profileJobMapper,
          profilePhotoObjectMapper,
          profilePositionObjectMapper,
          profilePositionInfoObjectMapper,
          profileSchoolObjectMapper)

  @Provides
  @Singleton
  fun facade(
      source: GetProfileSource,
      requestObjectMapper: GetProfileRequestObjectMapper,
      responseObjectMapper: GetProfileResponseObjectMapper) =
      GetProfileFacade(source, requestObjectMapper, responseObjectMapper)
}
