package se.magnus.microservices.core.insurancecompany;

import org.junit.Test;
import org.mapstruct.factory.Mappers;
import static org.junit.Assert.*;

import se.magnus.api.core.insuranceCompany.InsuranceCompany;
import se.magnus.microservices.core.insurancecompany.persistence.InsuranceCompanyEntity;
import se.magnus.microservices.core.insurancecompany.services.InsuranceCompanyMapper;

public class MapperTests {

    private InsuranceCompanyMapper mapper = Mappers.getMapper(InsuranceCompanyMapper.class);

    @Test
    public void mapperTests() {

        assertNotNull(mapper);

        InsuranceCompany api = new InsuranceCompany(1, "name","city","address","phoneNumber", "sa");

        InsuranceCompanyEntity entity = mapper.apiToEntity(api);

        assertEquals(api.getInsuranceCompanyId(), entity.getInsuranceCompanyId());
        assertEquals(api.getName(), entity.getName());
        assertEquals(api.getCity(), entity.getCity());
        assertEquals(api.getAddress(), entity.getAddress());
        assertEquals(api.getPhoneNumber(), entity.getPhoneNumber());

        InsuranceCompany api2 = mapper.entityToApi(entity);

        assertEquals(api.getInsuranceCompanyId(), api2.getInsuranceCompanyId());
        assertEquals(api.getName(),      api2.getName());
        assertEquals(api.getCity(),    api2.getCity());
        assertEquals(api.getAddress(),    api2.getAddress());
        assertEquals(api.getPhoneNumber(),    api2.getPhoneNumber());
        assertNull(api2.getServiceAddress());
    }
}
