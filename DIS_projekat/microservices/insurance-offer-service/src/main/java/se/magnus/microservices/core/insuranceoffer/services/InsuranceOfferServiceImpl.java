package se.magnus.microservices.core.insuranceoffer.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.magnus.api.core.insuranceOffer.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.util.exceptions.InvalidInputException;
import se.magnus.util.exceptions.NotFoundException;
import se.magnus.util.http.ServiceUtil;

import java.util.ArrayList;
import java.util.List;

@RestController
public class InsuranceOfferServiceImpl implements InsuranceOfferService {

    private static final Logger LOG = LoggerFactory.getLogger(InsuranceOfferServiceImpl.class);

    private final ServiceUtil serviceUtil;

    @Autowired
    public InsuranceOfferServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public List<InsuranceOffer> getInsuranceOffers(int insuranceCompanyId) {

        if (insuranceCompanyId < 1) throw new InvalidInputException("Invalid insuranceCompanyId: " + insuranceCompanyId);

        if (insuranceCompanyId == 213) {
            LOG.debug("No insurance offers found for insuranceCompanyId: {}", insuranceCompanyId);
            return  new ArrayList<>();
        }

        List<InsuranceOffer> list = new ArrayList<>();
        list.add(new InsuranceOffer(insuranceCompanyId, 1, "OfferName 1","typeOfferProgram 1", "typeInsuranceCoverage 1",570.67, "currencyOffer 1", serviceUtil.getServiceAddress()));
        list.add(new InsuranceOffer(insuranceCompanyId, 2, "OfferName 2", "typeOfferProgram 2", "typeInsuranceCoverage 2",570.67, "currencyOffer 2", serviceUtil.getServiceAddress()));
        list.add(new InsuranceOffer(insuranceCompanyId, 3, "OfferName 3", "typeOfferProgram 3", "typeInsuranceCoverage 3", 570.67, "currencyOffer 3", serviceUtil.getServiceAddress()));

        LOG.debug("/insuranceOffer response size: {}", list.size());

        return list;
    }
}