package se.magnus.microservices.core.insuranceoffer.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.api.core.insuranceOffer.*;
import se.magnus.microservices.core.insuranceoffer.persistence.*;
import se.magnus.util.exceptions.InvalidInputException;
import se.magnus.util.http.ServiceUtil;

import java.util.List;

@RestController
public class InsuranceOfferServiceImpl implements InsuranceOfferService {

    private static final Logger LOG = LoggerFactory.getLogger(InsuranceOfferServiceImpl.class);

    private ServiceUtil serviceUtil;
    
	private InsuranceOfferRepository repository;

	private InsuranceOfferMapper mapper;

    @Autowired
    public InsuranceOfferServiceImpl(ServiceUtil serviceUtil, InsuranceOfferRepository repository, InsuranceOfferMapper mapper) {
        this.serviceUtil = serviceUtil;
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
	public InsuranceOffer createInsuranceOffer(InsuranceOffer body) {
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
	public List<InsuranceOffer> getInsuranceOffers(int insuranceCompanyId) {

		if (insuranceCompanyId < 1)
			throw new InvalidInputException("Invalid insuranceCompanyId: " + insuranceCompanyId);

		List<InsuranceOfferEntity> entityList = repository.findByInsuranceCompanyId(insuranceCompanyId);
		List<InsuranceOffer> list = mapper.entityListToApiList(entityList);
		list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

		LOG.debug("getInsuranceOffer: response size: {}", list.size());

		return list;
	}

	@Override
	public void deleteInsuranceOffers(int insuranceCompanyId) {
		LOG.debug("deleteInsuranceOffers: tries to delete insurance offers for the insurance company with insuranceCompanyId: {}",
				insuranceCompanyId);
		repository.deleteAll(repository.findByInsuranceCompanyId(insuranceCompanyId));
	}
}