package se.magnus.microservices.core.employee.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
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
		try {
			EmployeeEntity entity = mapper.apiToEntity(body);
			EmployeeEntity newEntity = repository.save(entity);

			LOG.debug("createEmployee: created a employee entity: {}/{}", body.getInsuranceCompanyId(),
					body.getEmployeeId());
			return mapper.entityToApi(newEntity);

		} catch (DuplicateKeyException dke) {
			throw new InvalidInputException("Duplicate key, InsuranceCompanyId Id: " + body.getInsuranceCompanyId() + ", Employee Id:"
					+ body.getEmployeeId());
		}
	}

	@Override
	public List<Employee> getEmployees(int insuranceCompanyId) {

		if (insuranceCompanyId < 1)
			throw new InvalidInputException("Invalid insuranceCompanyId: " + insuranceCompanyId);

		List<EmployeeEntity> entityList = repository.findByInsuranceCompanyId(insuranceCompanyId);
		List<Employee> list = mapper.entityListToApiList(entityList);
		list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

		LOG.debug("getEmployees: response size: {}", list.size());

		return list;
	}

	@Override
	public void deleteEmployees(int insuranceCompanyId) {
		LOG.debug("deleteEmployees: tries to delete employees for the insurance company with insuranceCompanyId: {}",
				insuranceCompanyId);
		repository.deleteAll(repository.findByInsuranceCompanyId(insuranceCompanyId));
	}
}