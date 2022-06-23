package se.magnus.microservices.core.insurancecompany;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.test.StepVerifier;
import se.magnus.microservices.core.insurancecompany.persistence.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.IntStream.rangeClosed;
import static org.junit.Assert.*;
import static org.springframework.data.domain.Sort.Direction.ASC;

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

	/*
	 * @Test public void paging() {
	 * 
	 * repository.deleteAll();
	 * 
	 * List<InsuranceCompanyEntity> newInsuranceCompanies = rangeClosed(1001, 1010)
	 * .mapToObj(i -> new InsuranceCompanyEntity(i, "insuranceCompany"+i,"city"+i,
	 * "address"+i, "phone number"+i)) .collect(Collectors.toList());
	 * repository.saveAll(newInsuranceCompanies);
	 * 
	 * Pageable nextPage = PageRequest.of(0, 4, ASC, "insuranceCompanyId"); nextPage
	 * = testNextPage(nextPage, "[1001, 1002, 1003, 1004]", true); nextPage =
	 * testNextPage(nextPage, "[1005, 1006, 1007, 1008]", true); nextPage =
	 * testNextPage(nextPage, "[1009, 1010]", false); }
	 * 
	 * private Pageable testNextPage(Pageable nextPage, String
	 * expectedInsuranceCompanyIds, boolean expectsNextPage) {
	 * Page<InsuranceCompanyEntity> insuranceCompanyPage =
	 * repository.findAll(nextPage); assertEquals(expectedInsuranceCompanyIds,
	 * insuranceCompanyPage.getContent().stream().map(p ->
	 * p.getInsuranceCompanyId()).collect(Collectors.toList()).toString());
	 * assertEquals(expectsNextPage, insuranceCompanyPage.hasNext()); return
	 * insuranceCompanyPage.nextPageable(); }
	 */

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