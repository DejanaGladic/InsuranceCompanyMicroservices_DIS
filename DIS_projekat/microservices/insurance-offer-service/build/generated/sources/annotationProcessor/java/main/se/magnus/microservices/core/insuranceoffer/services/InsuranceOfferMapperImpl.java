package se.magnus.microservices.core.insuranceoffer.services;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;
import se.magnus.api.core.insuranceOffer.InsuranceOffer;
import se.magnus.microservices.core.insuranceoffer.persistence.InsuranceOfferEntity;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-05-28T02:27:17+0200",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 11.0.9 (Oracle Corporation)"
)
@Component
public class InsuranceOfferMapperImpl implements InsuranceOfferMapper {

    @Override
    public InsuranceOffer entityToApi(InsuranceOfferEntity entityInsuranceOffer) {
        if ( entityInsuranceOffer == null ) {
            return null;
        }

        InsuranceOffer insuranceOffer = new InsuranceOffer();

        return insuranceOffer;
    }

    @Override
    public InsuranceOfferEntity apiToEntity(InsuranceOffer apiInsuranceOffer) {
        if ( apiInsuranceOffer == null ) {
            return null;
        }

        InsuranceOfferEntity insuranceOfferEntity = new InsuranceOfferEntity();

        insuranceOfferEntity.setInsuranceOfferIdd( apiInsuranceOffer.getInsuranceOfferId() );
        insuranceOfferEntity.setInsuranceCompanyId( apiInsuranceOffer.getInsuranceCompanyId() );
        insuranceOfferEntity.setOfferName( apiInsuranceOffer.getOfferName() );
        insuranceOfferEntity.setTypeOfferProgram( apiInsuranceOffer.getTypeOfferProgram() );
        insuranceOfferEntity.setTypeInsuranceCoverage( apiInsuranceOffer.getTypeInsuranceCoverage() );
        insuranceOfferEntity.setPrice( apiInsuranceOffer.getPrice() );
        insuranceOfferEntity.setCurrencyOffer( apiInsuranceOffer.getCurrencyOffer() );

        return insuranceOfferEntity;
    }

    @Override
    public List<InsuranceOffer> entityListToApiList(List<InsuranceOfferEntity> entity) {
        if ( entity == null ) {
            return null;
        }

        List<InsuranceOffer> list = new ArrayList<InsuranceOffer>( entity.size() );
        for ( InsuranceOfferEntity insuranceOfferEntity : entity ) {
            list.add( entityToApi( insuranceOfferEntity ) );
        }

        return list;
    }

    @Override
    public List<InsuranceOfferEntity> apiListToEntityList(List<InsuranceOffer> api) {
        if ( api == null ) {
            return null;
        }

        List<InsuranceOfferEntity> list = new ArrayList<InsuranceOfferEntity>( api.size() );
        for ( InsuranceOffer insuranceOffer : api ) {
            list.add( apiToEntity( insuranceOffer ) );
        }

        return list;
    }
}
