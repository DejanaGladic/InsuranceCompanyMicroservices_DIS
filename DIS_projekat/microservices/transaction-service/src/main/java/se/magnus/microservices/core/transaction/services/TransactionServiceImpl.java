package se.magnus.microservices.core.transaction.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.magnus.api.core.transaction.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.util.exceptions.InvalidInputException;
import se.magnus.util.exceptions.NotFoundException;
import se.magnus.util.http.ServiceUtil;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

@RestController
public class TransactionServiceImpl implements TransactionService {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final ServiceUtil serviceUtil;

    @Autowired
    public TransactionServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public List<Transaction> getTransactions(int insuranceCompanyId) {

        if (insuranceCompanyId < 1) throw new InvalidInputException("Invalid insuranceCompanyId: " + insuranceCompanyId);

        if (insuranceCompanyId == 313) {
            LOG.debug("No transactions found for insuranceCompanyId: {}", insuranceCompanyId);
            return  new ArrayList<>();
        }

        List<Transaction> list = new ArrayList<>();
        list.add(new Transaction(insuranceCompanyId, 1, "typeTransaction 1",new Date(), 570.67, "currencyOffer 1", "accountNumber 1","policeNumber 1", serviceUtil.getServiceAddress()));
        list.add(new Transaction(insuranceCompanyId, 2, "typeTransaction 2",new Date(), 570.67, "currencyOffer 2","accountNumber 2","policeNumber 2", serviceUtil.getServiceAddress()));
        list.add(new Transaction(insuranceCompanyId, 3, "typeTransaction 3",new Date(), 570.67, "currencyOffer 3", "accountNumber 2","policeNumber 3",serviceUtil.getServiceAddress()));

        LOG.debug("/transaction response size: {}", list.size());

        return list;
    }
}