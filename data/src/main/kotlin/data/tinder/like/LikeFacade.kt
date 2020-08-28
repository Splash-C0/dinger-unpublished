package data.tinder.like

import data.ObjectMapper
import data.network.RequestFacade
import domain.like.DomainLikedRecommendationAnswer
import domain.recommendation.DomainRecommendationUser

internal class LikeFacade(
    source: LikeSource,
    requestMapper: ObjectMapper<DomainRecommendationUser, LikeRequestParameters>,
    responseMapper: ObjectMapper<LikeResponse, DomainLikedRecommendationAnswer>)
  : RequestFacade<DomainRecommendationUser, LikeRequestParameters, LikeResponse, DomainLikedRecommendationAnswer>(
    source, requestMapper, responseMapper)
