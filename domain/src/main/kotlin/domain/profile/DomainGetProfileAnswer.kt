package domain.profile

import java.util.Date

data class DomainGetProfileAnswer(
    val id: String?,
    val ageFilterMax: Int?,
    val ageFilterMin: Int?,
    val bio: String?,
    val birthDate: Date?,
    val blend: String?,
    val discoverable: Boolean?,
    val discoverableParty: String?,
    val distanceFilter: Int?,
    val email: String?,
    val emailSettings: DomainProfileEmailSettings?,
    val facebookId: String?,
    val gender: DomainProfileGender?,
    val genderFilter: DomainProfileGender?,
    val hideAds: Boolean?,
    val hideAge: Boolean?,
    val hideDistance: Boolean?,
    val spotifyConnected: Boolean?,
    val interestedIn: Iterable<DomainProfileGender>?,
    val jobs: Iterable<DomainProfileJob>?,
    val name: String?,
    val photos: Iterable<DomainProfilePhoto>?,
    val photoOptimizerEnabled: Boolean?,
    val photoOptimizerHasResult: Boolean?,
    val pingTime: Date?,
    val position: DomainProfilePosition?,
    val positionInfo: DomainProfilePositionInfo?,
    val schools: Iterable<DomainProfileSchool>?,
    val canCreateSquad: Boolean?)
