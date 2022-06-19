package se.magnus.microservices.core.employee.services;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;
import se.magnus.api.core.employee.Employee;
import se.magnus.microservices.core.employee.persistence.EmployeeEntity;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-06-17T16:13:14+0200",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 11.0.9 (Oracle Corporation)"
)
@Component
public class EmployeeMapperImpl implements EmployeeMapper {

    @Override
    public Employee entityToApi(EmployeeEntity entityEmployee) {
        if ( entityEmployee == null ) {
            return null;
        }

        Employee employee = new Employee();

        return employee;
    }

    @Override
    public EmployeeEntity apiToEntity(Employee apiEmployee) {
        if ( apiEmployee == null ) {
            return null;
        }

        EmployeeEntity employeeEntity = new EmployeeEntity();

        employeeEntity.setInsuranceCompanyId( apiEmployee.getInsuranceCompanyId() );
        employeeEntity.setEmployeeId( apiEmployee.getEmployeeId() );
        employeeEntity.setName( apiEmployee.getName() );
        employeeEntity.setSurname( apiEmployee.getSurname() );
        employeeEntity.setEducation( apiEmployee.getEducation() );
        employeeEntity.setSpecialization( apiEmployee.getSpecialization() );

        return employeeEntity;
    }

    @Override
    public List<Employee> entityListToApiList(List<EmployeeEntity> entity) {
        if ( entity == null ) {
            return null;
        }

        List<Employee> list = new ArrayList<Employee>( entity.size() );
        for ( EmployeeEntity employeeEntity : entity ) {
            list.add( entityToApi( employeeEntity ) );
        }

        return list;
    }

    @Override
    public List<EmployeeEntity> apiListToEntityList(List<Employee> api) {
        if ( api == null ) {
            return null;
        }

        List<EmployeeEntity> list = new ArrayList<EmployeeEntity>( api.size() );
        for ( Employee employee : api ) {
            list.add( apiToEntity( employee ) );
        }

        return list;
    }
}
