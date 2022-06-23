package se.magnus.microservices.core.employee.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

public interface EmployeeRepository extends ReactiveCrudRepository<EmployeeEntity, String> {
    Flux<EmployeeEntity> findByInsuranceCompanyId(int insuranceCompanyId);
    Flux<EmployeeEntity> findByName(String name);
}