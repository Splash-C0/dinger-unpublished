package data.tinder.dislike

import data.ObjectMapper
import data.network.RequestFacade
import domain.dislike.DomainDislikedRecommendationAnswer
import domain.recommendation.DomainRecommendationUser

internal class DislikeFacade(
    source: DislikeSource,
    requestMapper: ObjectMapper<DomainRecommendationUser, String>,
    responseMapper: ObjectMapper<DislikeResponse, DomainDislikedRecommendationAnswer>)
  : RequestFacade<DomainRecommendationUser, String, DislikeResponse, DomainDislikedRecommendationAnswer>(
    source, requestMapper, responseMapper)
