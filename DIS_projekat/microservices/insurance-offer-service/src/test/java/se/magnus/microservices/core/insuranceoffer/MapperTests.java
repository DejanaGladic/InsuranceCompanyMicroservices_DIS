package se.magnus.microservices.core.insuranceoffer;

import org.junit.Test;
import org.mapstruct.factory.Mappers;
import se.magnus.api.core.insuranceOffer.InsuranceOffer;
import se.magnus.api.core.transaction.Transaction;
import se.magnus.microservices.core.insuranceoffer.persistence.InsuranceOfferEntity;
import se.magnus.microservices.core.insuranceoffer.services.InsuranceOfferMapper;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class MapperTests {

    private InsuranceOfferMapper mapper = Mappers.getMapper(InsuranceOfferMapper.class);


    @Test
    public void mapperTests() {

        assertNotNull(mapper);

        InsuranceOffer api = new InsuranceOffer(1, 2, "offerName", "typeOffPr", "typeOffCov",550.55, "currT","adr");

        InsuranceOfferEntity entity = mapper.apiToEntity(api);

        assertEquals(api.getInsuranceCompanyId(), entity.getInsuranceCompanyId());
        assertEquals(api.getInsuranceOfferId(), entity.getInsuranceOfferId());
        assertEquals(api.getOfferName(), entity.getOfferName());
        assertEquals(api.getTypeOfferProgram(), entity.getTypeOfferProgram());
        assertEquals(api.getPrice(), entity.getPrice(),0.001);
        assertEquals(api.getTypeInsuranceCoverage(), entity.getTypeInsuranceCoverage());
        assertEquals(api.getCurrencyOffer(), entity.getCurrencyOffer());

        InsuranceOffer api2 = mapper.entityToApi(entity);

        assertEquals(api.getInsuranceCompanyId(), api2.getInsuranceCompanyId());
        assertEquals(api.getInsuranceOfferId(), api2.getInsuranceOfferId());
        assertEquals(api.getOfferName(), api2.getOfferName());
        assertEquals(api.getTypeOfferProgram(), api2.getTypeOfferProgram());
        assertEquals(api.getPrice(), api2.getPrice(),0.001);
        assertEquals(api.getTypeInsuranceCoverage(), api2.getTypeInsuranceCoverage());
        assertEquals(api.getCurrencyOffer(), api2.getCurrencyOffer());
        assertNull(api2.getServiceAddress());
    }

    @Test
    public void mapperListTests() {

        assertNotNull(mapper);

        InsuranceOffer api = new InsuranceOffer(1, 2, "offerName", "typeOffPr", "typeOffCov",550.55, "currT","adr");
        List<InsuranceOffer> apiList = Collections.singletonList(api);

        List<InsuranceOfferEntity> entityList = mapper.apiListToEntityList(apiList);
        assertEquals(apiList.size(), entityList.size());

        InsuranceOfferEntity entity = entityList.get(0);

        assertEquals(api.getInsuranceCompanyId(), entity.getInsuranceCompanyId());
        assertEquals(api.getInsuranceOfferId(), entity.getInsuranceOfferId());
        assertEquals(api.getOfferName(), entity.getOfferName());
        assertEquals(api.getTypeOfferProgram(), entity.getTypeOfferProgram());
        assertEquals(api.getPrice(), entity.getPrice(),0.001);
        assertEquals(api.getTypeInsuranceCoverage(), entity.getTypeInsuranceCoverage());
        assertEquals(api.getCurrencyOffer(), entity.getCurrencyOffer());

        List<InsuranceOffer> api2List = mapper.entityListToApiList(entityList);
        assertEquals(apiList.size(), api2List.size());

        InsuranceOffer api2 = api2List.get(0);

        assertEquals(api.getInsuranceCompanyId(), api2.getInsuranceCompanyId());
        assertEquals(api.getInsuranceOfferId(), api2.getInsuranceOfferId());
        assertEquals(api.getOfferName(), api2.getOfferName());
        assertEquals(api.getTypeOfferProgram(), api2.getTypeOfferProgram());
        assertEquals(api.getPrice(), api2.getPrice(),0.001);
        assertEquals(api.getTypeInsuranceCoverage(), api2.getTypeInsuranceCoverage());
        assertEquals(api.getCurrencyOffer(), api2.getCurrencyOffer());
        assertNull(api2.getServiceAddress());
    }
}
