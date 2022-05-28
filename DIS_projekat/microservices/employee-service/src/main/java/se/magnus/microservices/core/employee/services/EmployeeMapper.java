package se.magnus.microservices.core.employee.services;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import se.magnus.api.core.employee.Employee;
import se.magnus.microservices.core.employee.persistence.EmployeeEntity;
import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mappings({
        @Mapping(target = "serviceAddress", ignore = true)
    })
    Employee entityToApi(EmployeeEntity entityEmployee);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "version", ignore = true)
    })
    EmployeeEntity apiToEntity(Employee apiEmployee);
    
    List<Employee> entityListToApiList(List<EmployeeEntity> entity);
    List<EmployeeEntity> apiListToEntityList(List<Employee> api);
}