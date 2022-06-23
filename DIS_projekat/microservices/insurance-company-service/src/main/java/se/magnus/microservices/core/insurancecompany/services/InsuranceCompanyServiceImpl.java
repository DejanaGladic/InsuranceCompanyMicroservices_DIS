package se.magnus.microservices.core.insurancecompany.services;

import se.magnus.microservices.core.insurancecompany.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.*;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;
import se.magnus.api.core.insuranceCompany.*;
import reactor.core.publisher.Mono;
import se.magnus.util.exceptions.InvalidInputException;
import se.magnus.util.exceptions.NotFoundException;
import se.magnus.util.http.ServiceUtil;
import static reactor.core.publisher.Mono.error;

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
    	if (body.getInsuranceCompanyId() < 1) throw new InvalidInputException("Invalid insuranceCompanyId: " + body.getInsuranceCompanyId());

    	InsuranceCompanyEntity entity = mapper.apiToEntity(body);
        Mono<InsuranceCompany> newEntity = repository.save(entity)
            .log()
            .onErrorMap(
                DuplicateKeyException.class,
                ex -> new InvalidInputException("Duplicate key, insuranceCompanyId: " + body.getInsuranceCompanyId()))
            .map(e -> mapper.entityToApi(e));

        return newEntity.block();
    }

    @Override
    public Mono<InsuranceCompany> getInsuranceCompany(int insuranceCompanyId) {
         if (insuranceCompanyId < 1) throw new InvalidInputException("Invalid insuranceCompanyId: " + insuranceCompanyId);

         return repository.findByInsuranceCompanyId(insuranceCompanyId)
                 .switchIfEmpty(error(new NotFoundException("No insurance company found for insuranceCompanyId: " + insuranceCompanyId)))
                 .log()
                 .map(e -> mapper.entityToApi(e))
                 .map(e -> {e.setServiceAddress(serviceUtil.getServiceAddress()); return e;});       
    }
    
    @Override
    public void deleteInsuranceCompany(int insuranceCompanyId) {
    	 if (insuranceCompanyId < 1) throw new InvalidInputException("Invalid insuranceCompanyId: " + insuranceCompanyId);

         LOG.debug("deleteInsuranceCompany: tries to delete an entity with insuranceCompanyId: {}", insuranceCompanyId);
         repository.findByInsuranceCompanyId(insuranceCompanyId).log().map(e -> repository.delete(e)).flatMap(e -> e).block();        
    }
}