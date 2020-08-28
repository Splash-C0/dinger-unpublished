package data.autoswipe

import domain.recommendation.DomainRecommendationUser

internal class ProcessRecommendationActionFactoryWrapper(
    val delegate: (DomainRecommendationUser) -> ProcessRecommendationAction)
