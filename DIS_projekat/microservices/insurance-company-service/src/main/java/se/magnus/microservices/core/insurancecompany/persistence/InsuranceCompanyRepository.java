package se.magnus.microservices.core.insurancecompany.persistence;

import org.springframework.data.repository.*;

import java.util.Optional;

public interface InsuranceCompanyRepository extends PagingAndSortingRepository<InsuranceCompanyEntity, String> {
	//Optional = A container object which may or may not contain a non-null value. If a value is present, 
	//isPresent() will return true and get() will return the value.
    Optional<InsuranceCompanyEntity> findByInsuranceCompanyId(int insuranceCompanyId);
}