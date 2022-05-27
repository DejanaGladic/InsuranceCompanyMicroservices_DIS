package se.magnus.microservices.core.transaction.services;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import se.magnus.api.core.transaction.Transaction;
import se.magnus.microservices.core.transaction.persistence.Transaction;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mappings({
        @Mapping(target = "serviceAddress", ignore = true)
    })
    Transaction entityToApi(TransactionEntity entityTransaction);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "version", ignore = true)
    })
    TransactionEntity apiToEntity(Transaction apiTransaction);
}