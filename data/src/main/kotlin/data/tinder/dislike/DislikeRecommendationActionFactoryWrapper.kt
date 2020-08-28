package data.tinder.dislike

import domain.recommendation.DomainRecommendationUser

internal class DislikeRecommendationActionFactoryWrapper(
    val delegate: (DomainRecommendationUser) -> DislikeRecommendationAction)
