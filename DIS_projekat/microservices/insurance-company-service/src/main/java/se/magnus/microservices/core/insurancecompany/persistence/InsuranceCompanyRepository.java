package se.magnus.microservices.core.insurancecompany.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface InsuranceCompanyRepository extends extends ReactiveCrudRepository<InsuranceCompanyEntity, String> {
    Mono<InsuranceCompanyEntity> findByInsuranceCompanyId(int insuranceCompanyId);
}