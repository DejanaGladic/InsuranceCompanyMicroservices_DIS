package se.magnus.microservices.core.employee;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import org.junit.Test;
import org.mapstruct.factory.Mappers;
import se.magnus.api.core.employee.Employee;
import se.magnus.microservices.core.employee.persistence.EmployeeEntity;
import se.magnus.microservices.core.employee.services.EmployeeMapper;

public class MapperTests {

    private EmployeeMapper mapper = Mappers.getMapper(EmployeeMapper.class);

    @Test
    public void mapperTests() {

        assertNotNull(mapper);

        Employee api = new Employee(1, 2, "name", "surname","education", "specialization", "adr");

        EmployeeEntity entity = mapper.apiToEntity(api);

        assertEquals(api.getInsuranceCompanyId(), entity.getInsuranceCompanyId());
        assertEquals(api.getEmployeeId(), entity.getEmployeeId());
        assertEquals(api.getName(), entity.getName());
        assertEquals(api.getSurname(), entity.getSurname());
        assertEquals(api.getEducation(), entity.getEducation());
        assertEquals(api.getSpecialization(), entity.getSpecialization());

        Employee api2 = mapper.entityToApi(entity);

        assertEquals(api.getInsuranceCompanyId(), api2.getInsuranceCompanyId());
        assertEquals(api.getEmployeeId(), api2.getEmployeeId());
        assertEquals(api.getName(), api2.getName());
        assertEquals(api.getSurname(), api2.getSurname());
        assertEquals(api.getEducation(), api2.getEducation());
        assertEquals(api.getSpecialization(), api2.getSpecialization());
        assertNull(api2.getServiceAddress());
    }

    @Test
    public void mapperListTests() {

        assertNotNull(mapper);

        Employee api = new Employee(1, 2, "name", "surname","education", "specialization", "adr");
        List<Employee> apiList = Collections.singletonList(api);

        List<EmployeeEntity> entityList = mapper.apiListToEntityList(apiList);
        assertEquals(apiList.size(), entityList.size());

        EmployeeEntity entity = entityList.get(0);

        assertEquals(api.getInsuranceCompanyId(), entity.getInsuranceCompanyId());
        assertEquals(api.getEmployeeId(), entity.getEmployeeId());
        assertEquals(api.getName(), entity.getName());
        assertEquals(api.getSurname(), entity.getSurname());
        assertEquals(api.getEducation(), entity.getEducation());
        assertEquals(api.getSpecialization(), entity.getSpecialization());

        List<Employee> api2List = mapper.entityListToApiList(entityList);
        assertEquals(apiList.size(), api2List.size());

        Employee api2 = api2List.get(0);

        assertEquals(api.getInsuranceCompanyId(), api2.getInsuranceCompanyId());
        assertEquals(api.getEmployeeId(), api2.getEmployeeId());
        assertEquals(api.getName(), api2.getName());
        assertEquals(api.getSurname(), api2.getSurname());
        assertEquals(api.getEducation(), api2.getEducation());
        assertEquals(api.getSpecialization(), api2.getSpecialization());
        assertNull(api2.getServiceAddress());
    }
}
