package se.magnus.microservices.core.transaction;

import org.junit.Test;
import org.mapstruct.factory.Mappers;
import se.magnus.api.core.transaction.Transaction;
import se.magnus.microservices.core.transaction.persistence.TransactionEntity;
import se.magnus.microservices.core.transaction.services.TransactionMapper;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

public class MapperTests {

    private TransactionMapper mapper = Mappers.getMapper(TransactionMapper.class);


    @Test
    public void mapperTests() {

        assertNotNull(mapper);

        Transaction api = new Transaction(1, 2, "typeT", new Date(), 550.55, "currT","accN","plcNum","adr");

        TransactionEntity entity = mapper.apiToEntity(api);

        assertEquals(api.getInsuranceCompanyId(), entity.getInsuranceCompanyId());
        assertEquals(api.getTransactionId(), entity.getTransactionId());
        assertEquals(api.getTypeTransaction(), entity.getTypeTransaction());
        assertEquals(api.getDateTransaction(), entity.getDateTransaction());
        assertEquals(api.getAmount(), entity.getAmount(),0.001);
        assertEquals(api.getAccountNumber(), entity.getAccountNumber());
        assertEquals(api.getPolicyNumber(), entity.getPolicyNumber());

        Transaction api2 = mapper.entityToApi(entity);

        assertEquals(api.getInsuranceCompanyId(), api2.getInsuranceCompanyId());
        assertEquals(api.getTransactionId(), api2.getTransactionId());
        assertEquals(api.getTypeTransaction(), api2.getTypeTransaction());
        assertEquals(api.getDateTransaction(), api2.getDateTransaction());
        assertEquals(api.getAmount(), api2.getAmount(),0.001);
        assertEquals(api.getAccountNumber(), api2.getAccountNumber());
        assertEquals(api.getPolicyNumber(), api2.getPolicyNumber());
        assertNull(api2.getServiceAddress());
    }

    @Test
    public void mapperListTests() {

        assertNotNull(mapper);

        Transaction api = new Transaction(1, 2, "typeT", new Date(), 550.55, "currT","accN","plcNum","adr");
        List<Transaction> apiList = Collections.singletonList(api);

        List<TransactionEntity> entityList = mapper.apiListToEntityList(apiList);
        assertEquals(apiList.size(), entityList.size());

        TransactionEntity entity = entityList.get(0);

        assertEquals(api.getInsuranceCompanyId(), entity.getInsuranceCompanyId());
        assertEquals(api.getTransactionId(), entity.getTransactionId());
        assertEquals(api.getTypeTransaction(), entity.getTypeTransaction());
        assertEquals(api.getDateTransaction(), entity.getDateTransaction());
        assertEquals(api.getAmount(), entity.getAmount(),0.001);
        assertEquals(api.getAccountNumber(), entity.getAccountNumber());
        assertEquals(api.getPolicyNumber(), entity.getPolicyNumber());

        List<Transaction> api2List = mapper.entityListToApiList(entityList);
        assertEquals(apiList.size(), api2List.size());

        Transaction api2 = api2List.get(0);

        assertEquals(api.getInsuranceCompanyId(), api2.getInsuranceCompanyId());
        assertEquals(api.getTransactionId(), api2.getTransactionId());
        assertEquals(api.getTypeTransaction(), api2.getTypeTransaction());
        assertEquals(api.getDateTransaction(), api2.getDateTransaction());
        assertEquals(api.getAmount(), api2.getAmount(),0.001);
        assertEquals(api.getAccountNumber(), api2.getAccountNumber());
        assertEquals(api.getPolicyNumber(), api2.getPolicyNumber());
        assertNull(api2.getServiceAddress());
    }
}
