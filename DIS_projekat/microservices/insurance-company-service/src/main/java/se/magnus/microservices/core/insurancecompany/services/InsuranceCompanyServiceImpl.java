package se.magnus.microservices.core.insurancecompany.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.*;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;
import se.magnus.api.core.insuranceCompany.*;
import se.magnus.microservices.core.insurancecompany.persistence.*;
import se.magnus.util.exceptions.InvalidInputException;
import se.magnus.util.exceptions.NotFoundException;
import se.magnus.util.http.ServiceUtil;

@RestController
public class InsuranceCompanyServiceImpl implements InsuranceCompanyService {

    private static final Logger LOG = LoggerFactory.getLogger(InsuranceCompanyServiceImpl.class);

    private ServiceUtil serviceUtil;
    private InsuranceCompanyRepository repository;
    private InsuranceCompanyMapper mapper;

    @Autowired
    public InsuranceCompanyServiceImpl(InsuranceCompanyRepository repository, InsuranceCompanyMapper mapper, ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
        this.mapper = mapper;
        this.repository = repository;
    }
    
    @Override
    public InsuranceCompany createInsuranceCompany(InsuranceCompany body) {
    	try {
    		InsuranceCompanyEntity entity = mapper.apiToEntity(body);
    		InsuranceCompanyEntity newEntity = repository.save(entity);

            LOG.debug("createInsuranceCompany: entity created for insuranceCompanyId: {}", body.getInsuranceCompanyId());
            return mapper.entityToApi(newEntity);

        } catch (DuplicateKeyException dke) {
            throw new InvalidInputException("Duplicate key, InsuranceCompany Id: " + body.getInsuranceCompanyId());
        }
    }

    @Override
    public InsuranceCompany getInsuranceCompany(int insuranceCompanyId) {
         if (insuranceCompanyId < 1) throw new InvalidInputException("Invalid insuranceCompanyId: " + insuranceCompanyId);

         InsuranceCompanyEntity entity = repository.findByInsuranceCompanyId(insuranceCompanyId)
             .orElseThrow(() -> new NotFoundException("No insurance company found for insuranceCompanyId: " + insuranceCompanyId));

         InsuranceCompany response = mapper.entityToApi(entity);
         response.setServiceAddress(serviceUtil.getServiceAddress());

         LOG.debug("getInsuranceCompany: found insuranceCompanyId: {}", response.getInsuranceCompanyId());

         return response;
    }
    
    @Override
    public void deleteInsuranceCompany(int insuranceCompanyId) {
        LOG.debug("deleteInsuranceCompany: tries to delete an entity with insuranceCompanyId: {}", insuranceCompanyId);
        repository.findByInsuranceCompanyId(insuranceCompanyId).ifPresent(e -> repository.delete(e));
    }
}