package se.magnus.microservices.core.insurancecompany;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.OptimisticLockingFailureException;

import org.springframework.test.context.junit4.SpringRunner;
import reactor.test.StepVerifier;
import se.magnus.microservices.core.insurancecompany.persistence.*;

@RunWith(SpringRunner.class)
@DataMongoTest
public class PersistenceTests {

	@Autowired
	private InsuranceCompanyRepository repository;

	private InsuranceCompanyEntity savedEntity;

	@Before
	public void setupDb() {
		StepVerifier.create(repository.deleteAll()).verifyComplete();

		InsuranceCompanyEntity entity = new InsuranceCompanyEntity(1, "insuranceCompany 1", "city 1", "address 1",
				"phone number 1");
		StepVerifier.create(repository.save(entity)).expectNextMatches(createdEntity -> {
			savedEntity = createdEntity;
			return areInsuranceCompanyEqual(entity, savedEntity);
		}).verifyComplete();
	}

	@Test
	public void createInsuranceCompany() {

		InsuranceCompanyEntity newEntity = new InsuranceCompanyEntity(2, "insuranceCompany 2", "city 2", "address 2",
				"phone number 2");

		StepVerifier.create(repository.save(newEntity))
				.expectNextMatches(
						createdEntity -> newEntity.getInsuranceCompanyId() == createdEntity.getInsuranceCompanyId())
				.verifyComplete();

		StepVerifier.create(repository.findById(newEntity.getId()))
				.expectNextMatches(foundEntity -> areInsuranceCompanyEqual(newEntity, foundEntity)).verifyComplete();

		StepVerifier.create(repository.count()).expectNext(2l).verifyComplete();
	}

	@Test
	public void updateInsuranceCompany() {
		savedEntity.setName("insuranceCompany 2");

		StepVerifier.create(repository.save(savedEntity))
				.expectNextMatches(updatedEntity -> updatedEntity.getName().equals("insuranceCompany 2"))
				.verifyComplete();

		StepVerifier.create(repository.findById(savedEntity.getId())).expectNextMatches(
				foundEntity -> foundEntity.getVersion() == 1 && foundEntity.getName().equals("insuranceCompany 2"))
				.verifyComplete();
	}

	@Test
	public void deleteInsuranceCompany() {
		StepVerifier.create(repository.delete(savedEntity)).verifyComplete();
		StepVerifier.create(repository.existsById(savedEntity.getId())).expectNext(false).verifyComplete();
	}

	@Test
	public void getByInsuranceCompanyId() {

		StepVerifier.create(repository.findByInsuranceCompanyId(savedEntity.getInsuranceCompanyId()))
				.expectNextMatches(foundEntity -> areInsuranceCompanyEqual(savedEntity, foundEntity)).verifyComplete();
	}

	@Test
	public void optimisticLockError() {

		InsuranceCompanyEntity entity1 = repository.findById(savedEntity.getId()).block();
		InsuranceCompanyEntity entity2 = repository.findById(savedEntity.getId()).block();

		entity1.setName("n1");
		repository.save(entity1).block();

		StepVerifier.create(repository.save(entity2)).expectError(OptimisticLockingFailureException.class).verify();

		StepVerifier.create(repository.findById(savedEntity.getId()))
				.expectNextMatches(foundEntity -> foundEntity.getVersion() == 1 && foundEntity.getName().equals("n1"))
				.verifyComplete();
	}

	private boolean areInsuranceCompanyEqual(InsuranceCompanyEntity expectedEntity, InsuranceCompanyEntity actualEntity) {
		return ((expectedEntity.getId().equals(actualEntity.getId()))
				&& (expectedEntity.getVersion() == actualEntity.getVersion())
				&& (expectedEntity.getInsuranceCompanyId() == actualEntity.getInsuranceCompanyId())
				&& (expectedEntity.getName().equals(actualEntity.getName()))
				&& (expectedEntity.getCity().equals(actualEntity.getCity()))
				&& (expectedEntity.getAddress().equals(actualEntity.getAddress()))
				&& (expectedEntity.getPhoneNumber().equals(actualEntity.getPhoneNumber())));
	}
}