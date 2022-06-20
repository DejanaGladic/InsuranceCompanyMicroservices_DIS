package se.magnus.microservices.core.employee;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;
import se.magnus.microservices.core.employee.persistence.EmployeeEntity;
import se.magnus.microservices.core.employee.persistence.EmployeeRepository;
import static org.hamcrest.Matchers.hasSize;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataMongoTest
public class PersistenceTests {

	@Autowired
	private EmployeeRepository repository;

	private EmployeeEntity savedEmployeeEntity;

	@Before
	public void setupDb() {
		repository.deleteAll().block();

		EmployeeEntity entity = new EmployeeEntity(1, 1, "name1", "surname1", "education1", "specialization1");
		savedEmployeeEntity = repository.save(entity).block();

		assertEqualsEmployee(entity, savedEmployeeEntity);
	}

	@Test
	public void createEmployee() {

		EmployeeEntity newEntity = new EmployeeEntity(1, 2, "name2", "surname2", "education2", "specialization2");
		repository.save(newEntity).block();

		EmployeeEntity foundEntity = repository.findById(newEntity.getId()).block();
		assertEqualsEmployee(newEntity, foundEntity);

		assertEquals(2, (long) repository.count().block());
	}

	@Test
	public void updateEmployee() {
		savedEmployeeEntity.setName("name2");
		repository.save(savedEmployeeEntity).block();

		EmployeeEntity foundEntity = repository.findById(savedEmployeeEntity.getId()).block();
		assertEquals(1, (long) foundEntity.getVersion());
		assertEquals("name2", foundEntity.getName());
	}

	@Test
	public void deleteEmployee() {
		repository.delete(savedEmployeeEntity).block();
		assertFalse(repository.existsById(savedEmployeeEntity.getId()).block());
	}

	@Test
	public void getByInsuranceCompanyId() {
		List<EmployeeEntity> entityList = repository
				.findByInsuranceCompanyId(savedEmployeeEntity.getInsuranceCompanyId()).collectList().block();

		assertThat(entityList, hasSize(1));
		assertEqualsEmployee(savedEmployeeEntity, entityList.get(0));
	}

	// proverava verziju promene pa ako neko zeli da menja staru verziju ne da mu
	// (to je onaj property version iz persistence klasa)
	@Test
	public void optimisticLockError() {

		EmployeeEntity entity1 = repository.findById(savedEmployeeEntity.getId()).block();
		EmployeeEntity entity2 = repository.findById(savedEmployeeEntity.getId()).block();

		entity1.setName("name2");
		repository.save(entity1).block();

		try {
			entity2.setName("name3");
			repository.save(entity2).block();

			fail("Expected an OptimisticLockingFailureException");
		} catch (OptimisticLockingFailureException e) {
		}
		EmployeeEntity updatedEntity = repository.findById(savedEmployeeEntity.getId()).block();
		assertEquals(1, (int) updatedEntity.getVersion());
		assertEquals("name2", updatedEntity.getName());
	}

	private void assertEqualsEmployee(EmployeeEntity expectedEntity, EmployeeEntity actualEntity) {
		assertEquals(expectedEntity.getId(), actualEntity.getId());
		assertEquals(expectedEntity.getVersion(), actualEntity.getVersion());
		assertEquals(expectedEntity.getInsuranceCompanyId(), actualEntity.getInsuranceCompanyId());
		assertEquals(expectedEntity.getEmployeeId(), actualEntity.getEmployeeId());
		assertEquals(expectedEntity.getName(), actualEntity.getName());
		assertEquals(expectedEntity.getSurname(), actualEntity.getSurname());
		assertEquals(expectedEntity.getEducation(), actualEntity.getEducation());
		assertEquals(expectedEntity.getSpecialization(), actualEntity.getSpecialization());
	}
}