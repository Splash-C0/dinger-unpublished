package data.tinder.like

import domain.recommendation.DomainRecommendationUser

internal class LikeRecommendationActionFactoryWrapper(
    val delegate: (DomainRecommendationUser) -> LikeRecommendationAction)
