package data.tinder.profile

import data.ObjectMapper
import domain.profile.DomainGetProfileAnswer
import domain.profile.DomainProfileBounds
import domain.profile.DomainProfileCity
import domain.profile.DomainProfileCoordinates
import domain.profile.DomainProfileCountry
import domain.profile.DomainProfileEmailSettings
import domain.profile.DomainProfileGender
import domain.profile.DomainProfileJob
import domain.profile.DomainProfileJobTitle
import domain.profile.DomainProfilePhoto
import domain.profile.DomainProfilePosition
import domain.profile.DomainProfilePositionInfo
import domain.profile.DomainProfileProcessedPhoto
import domain.profile.DomainProfileSchool
import reporter.CrashReporter

internal class GetProfileResponseObjectMapper(
    crashReporter: CrashReporter,
    private val profileEmailSettingsMapper: ObjectMapper<ProfileEmailSettings, DomainProfileEmailSettings>,
    private val profileJobMapper: ObjectMapper<ProfileJob, DomainProfileJob>,
    private val profilePhotoObjectMapper: ObjectMapper<ProfilePhoto, DomainProfilePhoto>,
    private val profilePositionObjectMapper: ObjectMapper<ProfilePosition, DomainProfilePosition>,
    private val profilePositionInfoObjectMapper: ObjectMapper<ProfilePositionInfo, DomainProfilePositionInfo>,
    private val profileSchoolObjectMapper: ObjectMapper<ProfileSchool, DomainProfileSchool>)
  : ObjectMapper<GetProfileResponse, DomainGetProfileAnswer>(crashReporter) {
  override fun fromImpl(source: GetProfileResponse) = with(source) {
    DomainGetProfileAnswer(
        id = id,
        ageFilterMax = ageFilterMax,
        ageFilterMin = ageFilterMin,
        bio = bio,
        birthDate = birthDate,
        blend = blend,
        discoverable = discoverable,
        discoverableParty = discoverableParty,
        distanceFilter = distanceFilter,
        email = email,
        emailSettings = profileEmailSettingsMapper.from(emailSettings),
        facebookId = facebookId,
        gender = DomainProfileGender.fromGenderInt(gender),
        genderFilter = DomainProfileGender.fromGenderInt(genderFilter),
        hideAds = hideAds,
        hideAge = hideAge,
        hideDistance = hideDistance,
        spotifyConnected = spotifyConnected,
        interestedIn = interestedIn?.map { DomainProfileGender.fromGenderInt(it) } ?: emptySet(),
        jobs = jobs?.mapNotNull { profileJobMapper.from(it) } ?: emptySet(),
        name = name,
        photos = photos?.mapNotNull { profilePhotoObjectMapper.from(it) } ?: emptySet(),
        photoOptimizerEnabled = photoOptimizerEnabled,
        photoOptimizerHasResult = photoOptimizerHasResult,
        pingTime = pingTime,
        position = profilePositionObjectMapper.from(position),
        positionInfo = profilePositionInfoObjectMapper.from(positionInfo),
        schools = schools?.mapNotNull { profileSchoolObjectMapper.from(it) } ?: emptySet(),
        canCreateSquad = canCreateSquad)
  }
}

internal class ProfileEmailSettingsMapper(crashReporter: CrashReporter) :
    ObjectMapper<ProfileEmailSettings, DomainProfileEmailSettings>(crashReporter) {
  override fun fromImpl(source: ProfileEmailSettings) = with(source) {
    DomainProfileEmailSettings(newMatches, messages, promotions)
  }
}

internal class ProfileJobTitleMapper(crashReporter: CrashReporter) :
    ObjectMapper<ProfileJobTitle, DomainProfileJobTitle>(crashReporter) {
  override fun fromImpl(source: ProfileJobTitle) = with(source) {
    DomainProfileJobTitle(displayed, name)
  }
}

internal class ProfileJobMapper(
    crashReporter: CrashReporter,
    private val profileJobTitleMapper: ObjectMapper<ProfileJobTitle, DomainProfileJobTitle>)
  : ObjectMapper<ProfileJob, DomainProfileJob>(crashReporter) {
  override fun fromImpl(source: ProfileJob) = with(source) {
    DomainProfileJob(profileJobTitleMapper.from(title))
  }
}

internal class ProfilePhotoObjectMapper(
    crashReporter: CrashReporter,
    private val processedFileMapper: ObjectMapper<ProfileProcessedPhoto, DomainProfileProcessedPhoto>)
  : ObjectMapper<ProfilePhoto, DomainProfilePhoto>(crashReporter) {
  override fun fromImpl(source: ProfilePhoto) = with(source) {
    DomainProfilePhoto(
        id,
        url,
        fileName,
        extension,
        fbId,
        main,
        selectRate,
        processedFiles?.mapNotNull { processedFileMapper.from(it) } ?: emptySet(),
        xDistancePercent,
        xOffsetPercent,
        yDistancePercent,
        yOffsetPercent,
        shape)
  }
}

internal class ProfileProcessedPhotoObjectMapper(crashReporter: CrashReporter)
  : ObjectMapper<ProfileProcessedPhoto, DomainProfileProcessedPhoto>(crashReporter) {
  override fun fromImpl(source: ProfileProcessedPhoto) = with(source) {
    DomainProfileProcessedPhoto(heightPx, url, widthPx)
  }
}

internal class ProfilePositionObjectMapper(crashReporter: CrashReporter)
  : ObjectMapper<ProfilePosition, DomainProfilePosition>(crashReporter) {
  override fun fromImpl(source: ProfilePosition) = with(source) {
    DomainProfilePosition(at, lat, lon)
  }
}

internal class ProfilePositionInfoObjectMapper(
    crashReporter: CrashReporter,
    private val cityMapper: ObjectMapper<ProfileCity, DomainProfileCity>,
    private val countryMapper: ObjectMapper<ProfileCountry, DomainProfileCountry>)
  : ObjectMapper<ProfilePositionInfo, DomainProfilePositionInfo>(crashReporter) {
  override fun fromImpl(source: ProfilePositionInfo) = with(source) {
    DomainProfilePositionInfo(cityMapper.from(city), countryMapper.from(country))
  }
}

internal class ProfileCityObjectMapper(
    crashReporter: CrashReporter,
    private val boundMapper: ObjectMapper<ProfileBounds, DomainProfileBounds>)
  : ObjectMapper<ProfileCity, DomainProfileCity>(crashReporter) {
  override fun fromImpl(source: ProfileCity) = with(source) {
    DomainProfileCity(name, boundMapper.from(bounds))
  }
}

internal class ProfileBoundObjectMapper(
    crashReporter: CrashReporter,
    private val coordinateMapper: ObjectMapper<ProfileCoordinates, DomainProfileCoordinates>)
  : ObjectMapper<ProfileBounds, DomainProfileBounds>(crashReporter) {
  override fun fromImpl(source: ProfileBounds) = with(source) {
    DomainProfileBounds(coordinateMapper.from(ne), coordinateMapper.from(sw))
  }
}

internal class ProfileCoordinateMapper(crashReporter: CrashReporter)
  : ObjectMapper<ProfileCoordinates, DomainProfileCoordinates>(crashReporter) {
  override fun fromImpl(source: ProfileCoordinates) = with(source) {
    DomainProfileCoordinates(lat, lng)
  }
}

internal class ProfileCountryObjectMapper(
    crashReporter: CrashReporter,
    private val boundMapper: ObjectMapper<ProfileBounds, DomainProfileBounds>)
  : ObjectMapper<ProfileCountry, DomainProfileCountry>(crashReporter) {
  override fun fromImpl(source: ProfileCountry) = with(source) {
    DomainProfileCountry(name, countryCode, boundMapper.from(bounds))
  }
}

internal class ProfileSchoolObjectMapper(crashReporter: CrashReporter)
  : ObjectMapper<ProfileSchool, DomainProfileSchool>(crashReporter) {
  override fun fromImpl(source: ProfileSchool) = with(source) {
    DomainProfileSchool(displayed, name)
  }
}
