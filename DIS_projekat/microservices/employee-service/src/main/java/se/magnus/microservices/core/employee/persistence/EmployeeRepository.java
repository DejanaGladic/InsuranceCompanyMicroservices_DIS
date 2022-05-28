package se.magnus.microservices.core.employee.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EmployeeRepository extends CrudRepository<EmployeeEntity, String> {
    List<EmployeeEntity> findByInsuranceCompanyId(int insuranceCompanyId);
    EmployeeEntity findByEmployeeId(int employeeId);
}