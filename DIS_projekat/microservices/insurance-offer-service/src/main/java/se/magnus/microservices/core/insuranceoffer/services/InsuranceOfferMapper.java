package se.magnus.microservices.core.insuranceoffer.services;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import se.magnus.api.core.insuranceOffer.InsuranceOffer;
import se.magnus.microservices.core.insuranceoffer.persistence.InsuranceOfferEntity;

@Mapper(componentModel = "spring")
public interface InsuranceOfferMapper {

    @Mappings({
        @Mapping(target = "serviceAddress", ignore = true)
    })
    InsuranceOffer entityToApi(InsuranceOfferEntity entityInsuranceOffer);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "version", ignore = true)
    })
    InsuranceOfferEntity apiToEntity(InsuranceOffer apiInsuranceOffer);
}