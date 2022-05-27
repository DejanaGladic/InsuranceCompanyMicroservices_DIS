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
   		repository.deleteAll();

   		InsuranceCompanyEntity entity = new InsuranceCompanyEntity(1, "insuranceCompany 1","city 1", "address 1", "phone number 1");
        savedEntity = repository.save(entity);

        assertEqualsInsuranceCompany(entity, savedEntity);
    }


    @Test
   	public void createInsuranceCompany() {

    	InsuranceCompanyEntity newEntity = new InsuranceCompanyEntity(2, "insuranceCompany 2","city 2", "address 2", "phone number 2");
        repository.save(newEntity);

        InsuranceCompanyEntity foundEntity = repository.findById(newEntity.getId()).get();
        assertEqualsInsuranceCompany(newEntity, foundEntity);

        assertEquals(2, repository.count());
    }

    @Test
   	public void updateInsuranceCompany() {
        savedEntity.setName("insuranceCompany 2");
        repository.save(savedEntity);

        InsuranceCompanyEntity foundEntity = repository.findById(savedEntity.getId()).get();
        assertEquals(1, (long)foundEntity.getVersion());
        assertEquals("insuranceCompany 2", foundEntity.getName());
    }

    @Test
   	public void deleteInsuranceCompany() {
        repository.delete(savedEntity);
        assertFalse(repository.existsById(savedEntity.getId()));
    }

    @Test
   	public void getByInsuranceCompanyId() {
        Optional<InsuranceCompanyEntity> entity = repository.findByInsuranceCompanyId(savedEntity.getInsuranceCompanyId());

        assertTrue(entity.isPresent());
        assertEqualsInsuranceCompany(savedEntity, entity.get());
    }
    
	/*@Test(expected = DuplicateKeyException.class)
	public void duplicateError() {
		InsuranceCompanyEntity entity = new InsuranceCompanyEntity(2, "insuranceCompany 2","city 2", "address 2", "phone number 2");
		repository.save(entity);
	}*/

    @Test
   	public void optimisticLockError() {

    	InsuranceCompanyEntity entity1 = repository.findById(savedEntity.getId()).get();
    	InsuranceCompanyEntity entity2 = repository.findById(savedEntity.getId()).get();

        entity1.setName("n1");
        repository.save(entity1);

        try {
            entity2.setName("n2");
            repository.save(entity2);

            fail("Expected an OptimisticLockingFailureException");
        } catch (OptimisticLockingFailureException e) {}

        InsuranceCompanyEntity updatedEntity = repository.findById(savedEntity.getId()).get();
        assertEquals(1, (int)updatedEntity.getVersion());
        assertEquals("n1", updatedEntity.getName());
    }

    @Test
    public void paging() {

        repository.deleteAll();

        List<InsuranceCompanyEntity> newInsuranceCompanies = rangeClosed(1001, 1010)
            .mapToObj(i -> new InsuranceCompanyEntity(i, "insuranceCompany"+i,"city"+i, "address"+i, "phone number"+i))
            .collect(Collectors.toList());
        repository.saveAll(newInsuranceCompanies);

        Pageable nextPage = PageRequest.of(0, 4, ASC, "insuranceCompanyId");
        nextPage = testNextPage(nextPage, "[1001, 1002, 1003, 1004]", true);
        nextPage = testNextPage(nextPage, "[1005, 1006, 1007, 1008]", true);
        nextPage = testNextPage(nextPage, "[1009, 1010]", false);
    }

    private Pageable testNextPage(Pageable nextPage, String expectedInsuranceCompanyIds, boolean expectsNextPage) {
        Page<InsuranceCompanyEntity> insuranceCompanyPage = repository.findAll(nextPage);
        assertEquals(expectedInsuranceCompanyIds, insuranceCompanyPage.getContent().stream().map(p -> p.getInsuranceCompanyId()).collect(Collectors.toList()).toString());
        assertEquals(expectsNextPage, insuranceCompanyPage.hasNext());
        return insuranceCompanyPage.nextPageable();
    }

    private void assertEqualsInsuranceCompany(InsuranceCompanyEntity expectedEntity, InsuranceCompanyEntity actualEntity) {
        assertEquals(expectedEntity.getId(),               actualEntity.getId());
        assertEquals(expectedEntity.getVersion(),          actualEntity.getVersion());
        assertEquals(expectedEntity.getInsuranceCompanyId(),        actualEntity.getInsuranceCompanyId());
        assertEquals(expectedEntity.getName(),           actualEntity.getName());
        assertEquals(expectedEntity.getCity(),           actualEntity.getCity());
        assertEquals(expectedEntity.getAddress(),           actualEntity.getAddress());
        assertEquals(expectedEntity.getPhoneNumber(),           actualEntity.getPhoneNumber());
    }
}