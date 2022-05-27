package se.magnus.microservices.core.transaction;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import se.magnus.microservices.core.transaction.persistence.*;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;
import java.util.Date;


@RunWith(SpringRunner.class)
@DataJpaTest
@Transactional(propagation = NOT_SUPPORTED)
public class PersistenceTests {

	@Autowired
	private TransactionRepository repository;

	private TransactionEntity savedTransactionEntity;

	@Before
	public void setupDb() {
		repository.deleteAll();

		TransactionEntity entity = new TransactionEntity(1, 1, "typeTransaction 1",new Date(), 570.67, "currencyOffer 1", "accountNumber 1","policeNumber 1");
		savedTransactionEntity = repository.save(entity);

		assertEqualsTransaction(entity, savedTransactionEntity);
	}

	@Test
	public void createTransaction() {

		TransactionEntity newEntity = new TransactionEntity(1, 2, "typeTransaction 2",new Date(), 570.67, "currencyOffer 2", "accountNumber 2","policeNumber 2");
		repository.save(newEntity);

		TransactionEntity foundEntity = repository.findById(newEntity.getId()).get();
		assertEqualsTransaction(newEntity, foundEntity);

		assertEquals(2, repository.count());
	}

	@Test
	public void updateTransaction() {
		savedTransactionEntity.setTypeTransaction("typeTransaction 2");
		repository.save(savedTransactionEntity);

		TransactionEntity foundEntity = repository.findById(savedTransactionEntity.getId()).get();
		assertEquals(1, (long) foundEntity.getVersion());
		assertEquals("typeTransaction 2", foundEntity.getTypeTransaction());
	}

	@Test
	public void deleteTransaction() {
		repository.delete(savedTransactionEntity);
		assertFalse(repository.existsById(savedTransactionEntity.getId()));
	}

	@Test
	public void getByInsuranceCompanyId() {
		List<TransactionEntity> entityList = repository.findByInsuranceCompanyId(savedTransactionEntity.getInsuranceCompanyId());

        assertThat(entityList, hasSize(1));
        assertEqualsTransaction(savedTransactionEntity, entityList.get(0));
	}

	/*@Test(expected = DuplicateKeyException.class)
	public void duplicateError() {
		TransactionEntity entity = new TransactionEntity(1, 1, "typeTransaction 1",new Date(), 570.67, "currencyOffer 1", "accountNumber 1","policeNumber 1");
		repository.save(entity);
	}*/

	// proverava verziju promene pa ako neko zeli da menja staru verziju ne da mu
	// (to je onaj property version iz persistence klasa)
	@Test
	public void optimisticLockError() {

		TransactionEntity entity1 = repository.findById(savedTransactionEntity.getId()).get();
		TransactionEntity entity2 = repository.findById(savedTransactionEntity.getId()).get();

		entity1.setTypeTransaction("typeTransaction 2");
		repository.save(entity1);

		try {
			entity2.setTypeTransaction("typeTransaction 3");
			repository.save(entity2);

			fail("Expected an OptimisticLockingFailureException");
		} catch (OptimisticLockingFailureException e) {
		}
		TransactionEntity updatedEntity = repository.findById(savedTransactionEntity.getId()).get();
		assertEquals(1, (int) updatedEntity.getVersion());
		assertEquals("typeTransaction 2", updatedEntity.getTypeTransaction());
	}

	private void assertEqualsTransaction(TransactionEntity expectedEntity, TransactionEntity actualEntity) {
		assertEquals(expectedEntity.getId(), actualEntity.getId());
		assertEquals(expectedEntity.getVersion(), actualEntity.getVersion());
		assertEquals(expectedEntity.getInsuranceCompanyId(), actualEntity.getInsuranceCompanyId());
		
		assertEquals(expectedEntity.getTransactionId(), actualEntity.getTransactionId());
		assertEquals(expectedEntity.getTypeTransaction(), actualEntity.getTypeTransaction());
		assertEquals(expectedEntity.getDateTransaction(), actualEntity.getDateTransaction());
		assertEquals(expectedEntity.getAmount(), actualEntity.getAmount(), 0.001);
		assertEquals(expectedEntity.getCurrencyTransaction(), actualEntity.getCurrencyTransaction());
		assertEquals(expectedEntity.getAccountNumber(), actualEntity.getAccountNumber());
		assertEquals(expectedEntity.getPolicyNumber(), actualEntity.getPolicyNumber());
	}
}