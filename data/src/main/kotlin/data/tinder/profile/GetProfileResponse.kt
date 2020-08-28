package data.tinder.profile

import com.squareup.moshi.Json
import data.tinder.recommendation.Gender
import java.util.Date

internal class GetProfileResponse private constructor(
    @field:Json(name = "_id")
    val id: String?,
    @field:Json(name = "age_filter_max")
    val ageFilterMax: Int?,
    @field:Json(name = "age_filter_min")
    val ageFilterMin: Int?,
    @field:Json(name = "bio")
    val bio: String?,
    @field:Json(name = "birth_date")
    val birthDate: Date?,
    @field:Json(name = "blend")
    val blend: String?,
    @field:Json(name = "create_date")
    val createDate: Date?,
    @field:Json(name = "discoverable")
    val discoverable: Boolean?,
    @field:Json(name = "discoverable_party")
    val discoverableParty: String?,
    @field:Json(name = "distance_filter")
    val distanceFilter: Int?,
    @field:Json(name = "email")
    val email: String?,
    @field:Json(name = "email_settings")
    val emailSettings: ProfileEmailSettings?,
    @field:Json(name = "facebook_id")
    val facebookId: String?,
    @Gender
    @field:Json(name = "gender")
    val gender: Int?,
    @Gender
    @field:Json(name = "gender_filter")
    val genderFilter: Int?,
    @field:Json(name = "hide_ads")
    val hideAds: Boolean?,
    @field:Json(name = "hide_age")
    val hideAge: Boolean?,
    @field:Json(name = "hide_distance")
    val hideDistance: Boolean?,
    @field:Json(name = "spotify_connected")
    val spotifyConnected: Boolean?,
    @field:Json(name = "interested_in")
    @Gender
    val interestedIn: Array<Int>?,
    @field:Json(name = "jobs")
    val jobs: Array<ProfileJob>?,
    @field:Json(name = "name")
    val name: String,
    @field:Json(name = "photos")
    val photos: Array<ProfilePhoto>?,
    @field:Json(name = "photo_optimizer_enabled")
    val photoOptimizerEnabled: Boolean?,
    @field:Json(name = "photo_optimizer_has_result")
    val photoOptimizerHasResult: Boolean?,
    @field:Json(name = "ping_time")
    val pingTime: Date?,
    @field:Json(name = "pos")
    val position: ProfilePosition?,
    @field:Json(name = "pos_info")
    val positionInfo: ProfilePositionInfo?,
    @field:Json(name = "schools")
    val schools: Array<ProfileSchool>?,
    @field:Json(name = "can_create_squad")
    val canCreateSquad: Boolean?)
