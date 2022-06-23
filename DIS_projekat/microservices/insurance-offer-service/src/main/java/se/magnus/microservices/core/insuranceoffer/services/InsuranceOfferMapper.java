package se.magnus.microservices.core.insuranceoffer.services;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import se.magnus.api.core.insuranceOffer.*;
import se.magnus.microservices.core.insuranceoffer.persistence.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface InsuranceOfferMapper {

    @Mappings({
        @Mapping(target = "serviceAddress", ignore = true)
    })
    InsuranceOffer entityToApi(InsuranceOfferEntity entityInsuranceOffer);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "version", ignore = true),
    })
    InsuranceOfferEntity apiToEntity(InsuranceOffer apiInsuranceOffer);
    
    
    List<InsuranceOffer> entityListToApiList(List<InsuranceOfferEntity> entity);
    List<InsuranceOfferEntity> apiListToEntityList(List<InsuranceOffer> api);
}