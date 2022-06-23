package se.magnus.microservices.core.employee.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.magnus.api.core.employee.*;
import se.magnus.microservices.core.employee.persistence.*;
import se.magnus.util.exceptions.InvalidInputException;
import se.magnus.util.http.ServiceUtil;

import java.util.List;

@RestController
public class EmployeeServiceImpl implements EmployeeService {

	private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

	private ServiceUtil serviceUtil;

	private EmployeeRepository repository;

	private EmployeeMapper mapper;

	@Autowired
	public EmployeeServiceImpl(ServiceUtil serviceUtil, EmployeeRepository repository, EmployeeMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
		this.serviceUtil = serviceUtil;
	}

	@Override
	public Employee createEmployee(Employee body) {
		if (body.getInsuranceCompanyId() < 1) throw new InvalidInputException("Invalid insuranceCompanyId: " + body.getInsuranceCompanyId());

		EmployeeEntity entity = mapper.apiToEntity(body);
        Mono<Employee> newEntity = repository.save(entity)
            .log()
            .onErrorMap(
                DuplicateKeyException.class,
                ex -> new InvalidInputException("Duplicate key, Insurance Company Id: " + body.getInsuranceCompanyId() + ", Employee Id:" + body.getEmployeeId()))
            .map(e -> mapper.entityToApi(e));
        return newEntity.block();
	}

	@Override
	public Flux<Employee> getEmployees(int insuranceCompanyId) {

		if (insuranceCompanyId < 1)
			throw new InvalidInputException("Invalid insuranceCompanyId: " + insuranceCompanyId);

		return repository.findByInsuranceCompanyId(insuranceCompanyId)
	            .log()
	            .map(e -> mapper.entityToApi(e))
	            .map(e -> {e.setServiceAddress(serviceUtil.getServiceAddress()); return e;});		
	}

	@Override
	public void deleteEmployees(int insuranceCompanyId) {
		
		if (insuranceCompanyId < 1)
			throw new InvalidInputException("Invalid insuranceCompanyId: " + insuranceCompanyId);
		
        LOG.debug("deleteEmployees: tries to delete employees for the insurance company with insuranceCompanyId: {}", insuranceCompanyId);
        repository.deleteAll(repository.findByInsuranceCompanyId(insuranceCompanyId)).block();        
	}
}