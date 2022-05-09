package se.magnus.microservices.core.insurancecompany.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.api.core.insuranceCompany.*;
import se.magnus.util.exceptions.InvalidInputException;
import se.magnus.util.exceptions.NotFoundException;
import se.magnus.util.http.ServiceUtil;

@RestController
public class InsuranceCompanyServiceImpl implements InsuranceCompanyService {

    private static final Logger LOG = LoggerFactory.getLogger(InsuranceCompanyServiceImpl.class);

    private final ServiceUtil serviceUtil;

    @Autowired
    public InsuranceCompanyServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public InsuranceCompany getInsuranceCompany(int insuranceCompanyId) {
        LOG.debug("/product return the found product for insuranceCompanyId={}", insuranceCompanyId);

        if (insuranceCompanyId < 1) throw new InvalidInputException("Invalid insuranceCompanyId: " + insuranceCompanyId);

        if (insuranceCompanyId == 13) throw new NotFoundException("No insurance company found for insuranceCompanyId: " + insuranceCompanyId);

        return new InsuranceCompany(insuranceCompanyId, "insuranceCompany 1","city 1", "address 1", "phone number 1", serviceUtil.getServiceAddress());
    }
}