package se.magnus.microservices.core.insurancecompany.services;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import se.magnus.api.core.insuranceCompany.InsuranceCompany;
import se.magnus.microservices.core.insurancecompany.persistence.InsuranceCompanyEntity;

@Mapper(componentModel = "spring")
public interface InsuranceCompanyMapper {

    @Mappings({
        @Mapping(target = "serviceAddress", ignore = true)
    })
    InsuranceCompany entityToApi(InsuranceCompanyEntity entityInsuranceCompany);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "version", ignore = true)
    })
    InsuranceCompanyEntity apiToEntity(InsuranceCompany apiInsuranceCompany);
}