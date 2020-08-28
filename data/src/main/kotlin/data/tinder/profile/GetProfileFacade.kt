package data.tinder.profile

import data.ObjectMapper
import data.network.RequestFacade
import domain.profile.DomainGetProfileAnswer

internal class GetProfileFacade(
    source: GetProfileSource,
    requestMapper: ObjectMapper<Unit, Unit>,
    responseMapper: ObjectMapper<GetProfileResponse, DomainGetProfileAnswer>)
  : RequestFacade<Unit, Unit, GetProfileResponse, DomainGetProfileAnswer>(source, requestMapper, responseMapper)
