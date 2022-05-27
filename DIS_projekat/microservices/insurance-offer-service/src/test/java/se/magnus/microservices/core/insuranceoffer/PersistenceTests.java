package se.magnus.microservices.core.insuranceoffer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import se.magnus.microservices.core.insuranceoffer.persistence.*;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

@RunWith(SpringRunner.class)
@DataJpaTest
@Transactional(propagation = NOT_SUPPORTED)
public class PersistenceTests {

	@Autowired
	private InsuranceOfferRepository repository;

	private InsuranceOfferEntity savedInsuranceOfferEntity;

	@Before
	public void setupDb() {
		repository.deleteAll();

		InsuranceOfferEntity entity = new InsuranceOfferEntity(1, 1, "name1", "typeOfferProgram1", "typeInsuranceCoverage1", 100.58, "currencyOffer1");
		savedInsuranceOfferEntity = repository.save(entity);

		assertEqualsInsuranceOffer(entity, savedInsuranceOfferEntity);
	}

	@Test
	public void createInsuranceOffer() {

		InsuranceOfferEntity newEntity = new InsuranceOfferEntity(1, 2, "name2", "typeOfferProgram2", "typeInsuranceCoverage2", 100.58, "currencyOffer2");
		repository.save(newEntity);

		InsuranceOfferEntity foundEntity = repository.findById(newEntity.getId()).get();
		assertEqualsInsuranceOffer(newEntity, foundEntity);

		assertEquals(2, repository.count());
	}

	@Test
	public void updateInsuranceOffer() {
		savedInsuranceOfferEntity.setOfferName("name2");
		repository.save(savedInsuranceOfferEntity);

		InsuranceOfferEntity foundEntity = repository.findById(savedInsuranceOfferEntity.getId()).get();
		assertEquals(1, (long) foundEntity.getVersion());
		assertEquals("name2", foundEntity.getOfferName());
	}

	@Test
	public void deleteInsuranceOffer() {
		repository.delete(savedInsuranceOfferEntity);
		assertFalse(repository.existsById(savedInsuranceOfferEntity.getId()));
	}

	@Test
	public void getByInsuranceCompanyId() {
		List<InsuranceOfferEntity> entityList = repository.findByInsuranceCompanyId(savedInsuranceOfferEntity.getInsuranceCompanyId());

        assertThat(entityList, hasSize(1));
        assertEqualsInsuranceOffer(savedInsuranceOfferEntity, entityList.get(0));
	}
	
	/*@Test(expected = DuplicateKeyException.class)
	public void duplicateError() {
		InsuranceOfferEntity entity = new InsuranceOfferEntity(1, 1, "name2", "typeOfferProgram2", "typeInsuranceCoverage2", 100.58, "currencyOffer2");
		repository.save(entity);
	}*/

	// proverava verziju promene pa ako neko zeli da menja staru verziju ne da mu
	// (to je onaj property version iz persistence klasa)
	@Test
	public void optimisticLockError() {

		InsuranceOfferEntity entity1 = repository.findById(savedInsuranceOfferEntity.getId()).get();
		InsuranceOfferEntity entity2 = repository.findById(savedInsuranceOfferEntity.getId()).get();

		entity1.setOfferName("name2");
		repository.save(entity1);

		try {
			entity2.setOfferName("name3");
			repository.save(entity2);

			fail("Expected an OptimisticLockingFailureException");
		} catch (OptimisticLockingFailureException e) {
		}
		InsuranceOfferEntity updatedEntity = repository.findById(savedInsuranceOfferEntity.getId()).get();
		assertEquals(1, (int) updatedEntity.getVersion());
		assertEquals("name2", updatedEntity.getOfferName());
	}

	private void assertEqualsInsuranceOffer(InsuranceOfferEntity expectedEntity, InsuranceOfferEntity actualEntity) {
		assertEquals(expectedEntity.getId(), actualEntity.getId());
		assertEquals(expectedEntity.getVersion(), actualEntity.getVersion());
		assertEquals(expectedEntity.getInsuranceCompanyId(), actualEntity.getInsuranceCompanyId());
		assertEquals(expectedEntity.getInsuranceOfferId(), actualEntity.getInsuranceOfferId());
		assertEquals(expectedEntity.getOfferName(), actualEntity.getOfferName());
		assertEquals(expectedEntity.getTypeOfferProgram(), actualEntity.getTypeOfferProgram());
		assertEquals(expectedEntity.getTypeInsuranceCoverage(), actualEntity.getTypeInsuranceCoverage());
		assertEquals(expectedEntity.getPrice(), actualEntity.getPrice(), 0.001);
		assertEquals(expectedEntity.getCurrencyOffer(), actualEntity.getCurrencyOffer());
	}
}