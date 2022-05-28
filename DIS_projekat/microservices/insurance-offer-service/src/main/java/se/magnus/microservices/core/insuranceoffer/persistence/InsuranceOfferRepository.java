package se.magnus.microservices.core.insuranceoffer.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface InsuranceOfferRepository extends CrudRepository<InsuranceOfferEntity, Integer> {

    @Transactional(readOnly = true)
    List<InsuranceOfferEntity> findByInsuranceCompanyId(int insuranceCompanyId);
    @Transactional(readOnly = true)
    InsuranceOfferEntity findByInsuranceOfferId(int insuranceOfferId);
}