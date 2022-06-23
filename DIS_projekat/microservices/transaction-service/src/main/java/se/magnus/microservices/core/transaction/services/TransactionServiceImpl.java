package se.magnus.microservices.core.transaction.services;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import se.magnus.api.core.transaction.*;
import se.magnus.microservices.core.transaction.persistence.*;
import se.magnus.util.exceptions.InvalidInputException;
import se.magnus.util.http.ServiceUtil;
import java.util.List;
import java.util.function.Supplier;
import static java.util.logging.Level.FINE;

@RestController
public class TransactionServiceImpl implements TransactionService {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private ServiceUtil serviceUtil;
	private TransactionRepository repository;
	private TransactionMapper mapper;

	private final Scheduler scheduler;

    @Autowired
    public TransactionServiceImpl(Scheduler scheduler, ServiceUtil serviceUtil, TransactionRepository repository, TransactionMapper mapper) {
		this.scheduler = scheduler;
		this.serviceUtil = serviceUtil;
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
   	public Transaction createTransaction(Transaction body) {
		if (body.getInsuranceCompanyId() < 1) throw new InvalidInputException("Invalid insuranceCompanyId: " + body.getInsuranceCompanyId());
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
   	public Flux<Transaction> getTransactions(int insuranceCompanyId) {

   		if (insuranceCompanyId < 1)
   			throw new InvalidInputException("Invalid insuranceCompanyId: " + insuranceCompanyId);

		LOG.info("Will get transactions for insurance company with id={}", insuranceCompanyId);

		return asyncFlux(() -> Flux.fromIterable(getByInsuranceCompanyId(insuranceCompanyId))).log(null, FINE);
   	}

	protected List<Transaction> getByInsuranceCompanyId(int insuranceCompanyId) {

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

	private <T> Flux<T> asyncFlux(Supplier<Publisher<T>> publisherSupplier) {
		return Flux.defer(publisherSupplier).subscribeOn(scheduler);
	}
}