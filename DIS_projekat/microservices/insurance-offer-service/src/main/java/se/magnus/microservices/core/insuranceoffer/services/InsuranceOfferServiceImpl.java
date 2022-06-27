package se.magnus.microservices.core.insuranceoffer.services;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import se.magnus.api.core.insuranceOffer.*;
import se.magnus.microservices.core.insuranceoffer.persistence.*;
import se.magnus.util.exceptions.InvalidInputException;
import se.magnus.util.http.ServiceUtil;

import java.util.List;
import java.util.function.Supplier;
import static java.util.logging.Level.FINE;

@RestController
public class InsuranceOfferServiceImpl implements InsuranceOfferService {

    private static final Logger LOG = LoggerFactory.getLogger(InsuranceOfferServiceImpl.class);

    private ServiceUtil serviceUtil;
    
	private InsuranceOfferRepository repository;

	private InsuranceOfferMapper mapper;

	private final Scheduler scheduler;

    @Autowired
    public InsuranceOfferServiceImpl(Scheduler scheduler,ServiceUtil serviceUtil, InsuranceOfferRepository repository, InsuranceOfferMapper mapper) {
		this.scheduler = scheduler;
        this.serviceUtil = serviceUtil;
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
	public InsuranceOffer createInsuranceOffer(InsuranceOffer body) {
		if (body.getInsuranceCompanyId() < 1) throw new InvalidInputException("Invalid insuranceCompanyId: " + body.getInsuranceCompanyId());

		try {
			InsuranceOfferEntity entity = mapper.apiToEntity(body);
			InsuranceOfferEntity newEntity = repository.save(entity);

			LOG.debug("createInsuranceOffer: created a insurance offer entity: {}/{}", body.getInsuranceCompanyId(),
					body.getInsuranceOfferId());
			return mapper.entityToApi(newEntity);

		} catch (DataIntegrityViolationException dive) {
			throw new InvalidInputException("Duplicate key, InsuranceCompanyId Id: " + body.getInsuranceCompanyId() + ", InsuranceOffer Id:"
					+ body.getInsuranceOfferId());
        }
	}

	@Override
	public Flux<InsuranceOffer> getInsuranceOffers(int insuranceCompanyId) {

		if (insuranceCompanyId < 1)
			throw new InvalidInputException("Invalid insuranceCompanyId: " + insuranceCompanyId);

		LOG.info("Will get insurance offers for insurance company with id={}", insuranceCompanyId);

		return asyncFlux(() -> Flux.fromIterable(getByInsuranceCompanyId(insuranceCompanyId))).log(null, FINE);
	}

	protected List<InsuranceOffer> getByInsuranceCompanyId(int insuranceCompanyId) {

		List<InsuranceOfferEntity> entityList = repository.findByInsuranceCompanyId(insuranceCompanyId);
		List<InsuranceOffer> list = mapper.entityListToApiList(entityList);
		list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

		LOG.debug("getInsuranceOffers: response size: {}", list.size());

		return list;
	}

	@Override
	public void deleteInsuranceOffers(int insuranceCompanyId) {
		if (insuranceCompanyId < 1) throw new InvalidInputException("Invalid insuranceCompanyId: " + insuranceCompanyId);
		LOG.debug("deleteInsuranceOffers: tries to delete insurance offers for the insurance company with insuranceCompanyId: {}",
				insuranceCompanyId);
		repository.deleteAll(repository.findByInsuranceCompanyId(insuranceCompanyId));
	}

	private <T> Flux<T> asyncFlux(Supplier<Publisher<T>> publisherSupplier) {
		return Flux.defer(publisherSupplier).subscribeOn(scheduler);
	}
}