package domain.recommendation

import java.util.Date

data class DomainRecommendationUser(
    val bio: String?,
    val distanceMiles: Int,
    val commonFriends: Iterable<DomainRecommendationCommonFriend>,
    val commonFriendCount: Int,
    val commonLikes: Iterable<DomainRecommendationLike>,
    val commonLikeCount: Int,
    val id: String,
    val birthDate: Date?,
    val name: String,
    val instagram: DomainRecommendationInstagram?,
    val teaser: DomainRecommendationTeaser?,
    val spotifyThemeTrack: DomainRecommendationSpotifyThemeTrack?,
    val gender: DomainRecommendationGender,
    val birthDateInfo: String,
    val contentHash: String?,
    val groupMatched: Boolean,
    val sNumber: Long,
    val liked: Boolean = false, // Recommendations are not liked by default
    var matched: Boolean = false, // Nor matched by default
    val photos: Iterable<DomainRecommendationPhoto>?,
    val jobs: Iterable<DomainRecommendationJob>,
    val schools: Iterable<DomainRecommendationSchool>,
    val teasers: Iterable<DomainRecommendationTeaser>)
