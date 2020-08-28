package domain.recommendation

import java.util.Date

data class DomainRecommendationInstagram(
    val profilePictureUrl: String?,
    val lastFetchTime: Date?,
    val mediaCount: Int,
    val completedInitialFetch: Boolean,
    val username: String?,
    val photos: Iterable<DomainRecommendationInstagramPhoto>?)
