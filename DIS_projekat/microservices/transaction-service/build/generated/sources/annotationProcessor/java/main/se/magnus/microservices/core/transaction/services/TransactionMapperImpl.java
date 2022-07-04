package se.magnus.microservices.core.transaction.services;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;
import se.magnus.api.core.transaction.Transaction;
import se.magnus.microservices.core.transaction.persistence.TransactionEntity;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2022-07-04T14:00:28+0200",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 1.8.0_271 (Oracle Corporation)"
)
@Component
public class TransactionMapperImpl implements TransactionMapper {

    @Override
    public Transaction entityToApi(TransactionEntity entityTransaction) {
        if ( entityTransaction == null ) {
            return null;
        }

        Transaction transaction = new Transaction();

        transaction.setInsuranceCompanyId( entityTransaction.getInsuranceCompanyId() );
        transaction.setTransactionId( entityTransaction.getTransactionId() );
        transaction.setTypeTransaction( entityTransaction.getTypeTransaction() );
        transaction.setDateTransaction( entityTransaction.getDateTransaction() );
        transaction.setAmount( entityTransaction.getAmount() );
        transaction.setCurrencyTransaction( entityTransaction.getCurrencyTransaction() );
        transaction.setAccountNumber( entityTransaction.getAccountNumber() );
        transaction.setPolicyNumber( entityTransaction.getPolicyNumber() );

        return transaction;
    }

    @Override
    public TransactionEntity apiToEntity(Transaction apiTransaction) {
        if ( apiTransaction == null ) {
            return null;
        }

        TransactionEntity transactionEntity = new TransactionEntity();

        transactionEntity.setInsuranceCompanyId( apiTransaction.getInsuranceCompanyId() );
        transactionEntity.setTransactionId( apiTransaction.getTransactionId() );
        transactionEntity.setTypeTransaction( apiTransaction.getTypeTransaction() );
        transactionEntity.setDateTransaction( apiTransaction.getDateTransaction() );
        transactionEntity.setAmount( apiTransaction.getAmount() );
        transactionEntity.setCurrencyTransaction( apiTransaction.getCurrencyTransaction() );
        transactionEntity.setAccountNumber( apiTransaction.getAccountNumber() );
        transactionEntity.setPolicyNumber( apiTransaction.getPolicyNumber() );

        return transactionEntity;
    }

    @Override
    public List<Transaction> entityListToApiList(List<TransactionEntity> entity) {
        if ( entity == null ) {
            return null;
        }

        List<Transaction> list = new ArrayList<Transaction>( entity.size() );
        for ( TransactionEntity transactionEntity : entity ) {
            list.add( entityToApi( transactionEntity ) );
        }

        return list;
    }

    @Override
    public List<TransactionEntity> apiListToEntityList(List<Transaction> api) {
        if ( api == null ) {
            return null;
        }

        List<TransactionEntity> list = new ArrayList<TransactionEntity>( api.size() );
        for ( Transaction transaction : api ) {
            list.add( apiToEntity( transaction ) );
        }

        return list;
    }
}
