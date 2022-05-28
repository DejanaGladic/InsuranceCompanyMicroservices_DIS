package se.magnus.microservices.core.transaction.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.api.core.transaction.*;
import se.magnus.microservices.core.transaction.persistence.*;
import se.magnus.util.exceptions.InvalidInputException;
import se.magnus.util.exceptions.NotFoundException;
import se.magnus.util.http.ServiceUtil;
import java.util.List;
import java.util.Date;
import java.util.List;

@RestController
public class TransactionServiceImpl implements TransactionService {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private ServiceUtil serviceUtil;
	private TransactionRepository repository;
	private TransactionMapper mapper;

    @Autowired
    public TransactionServiceImpl(ServiceUtil serviceUtil, TransactionRepository repository, TransactionMapper mapper) {
        this.serviceUtil = serviceUtil;
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
   	public Transaction createTransaction(Transaction body) {
   		try {
   			TransactionEntity entity = mapper.apiToEntity(body);
   			TransactionEntity newEntity = repository.save(entity);

   			LOG.debug("createTransaction: created a insurance transaction entity: {}/{}", body.getInsuranceCompanyId(),
   					body.getTransactionId());
   			return mapper.entityToApi(newEntity);

   		} catch (DataIntegrityViolationException dive) {
   			throw new InvalidInputException("Duplicate key, InsuranceCompanyId Id: " + body.getInsuranceCompanyId() + ", Transaction Id:"
   					+ body.getTransactionId());
   		}
   	}

   	@Override
   	public List<Transaction> getTransactions(int insuranceCompanyId) {

   		if (insuranceCompanyId < 1)
   			throw new InvalidInputException("Invalid insuranceCompanyId: " + insuranceCompanyId);

   		List<TransactionEntity> entityList = repository.findByInsuranceCompanyId(insuranceCompanyId);
   		List<Transaction> list = mapper.entityListToApiList(entityList);
   		list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

   		LOG.debug("getTransactions: response size: {}", list.size());

   		return list;
   	}

   	@Override
   	public void deleteTransactions(int insuranceCompanyId) {
   		LOG.debug("deleteTransactions: tries to delete transactions for the insurance company with insuranceCompanyId: {}",
   				insuranceCompanyId);
   		repository.deleteAll(repository.findByInsuranceCompanyId(insuranceCompanyId));
   	}
}