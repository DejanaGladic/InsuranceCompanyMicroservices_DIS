package se.magnus.microservices.core.transaction.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TransactionRepository extends CrudRepository<TransactionEntity, Integer> {

    @Transactional(readOnly = true)
    List<TransactionEntity> findByInsuranceCompanyId(int insuranceCompanyId);
}