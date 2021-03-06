package se.magnus.microservices.core.transaction.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import se.magnus.api.core.insuranceCompany.*;
import se.magnus.api.core.transaction.Transaction;
import se.magnus.api.core.transaction.TransactionService;
import se.magnus.api.event.Event;
import se.magnus.util.exceptions.EventProcessingException;

@EnableBinding(Sink.class)
public class MessageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);

    private final TransactionService transactionService;

    @Autowired
    public MessageProcessor (TransactionService transactionService){
        this.transactionService = transactionService;
    }

    @StreamListener(target = Sink.INPUT)
    public void process(Event<Integer, Transaction> event) {

        LOG.info("Process message created at {}...", event.getEventCreatedAt());

        switch (event.getEventType()) {

            case CREATE:
                Transaction transaction = event.getData();
                LOG.info("Create transaction with ID: {}", transaction.getInsuranceCompanyId());
                transactionService.createTransaction(transaction);
                break;

            case DELETE:
                int insuranceCompanyId = event.getKey();
                LOG.info("Delete transactions with insuranceCompanyId: {}", insuranceCompanyId);
                transactionService.deleteTransactions(insuranceCompanyId);
                break;

            default:
                String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
                LOG.warn(errorMessage);
                throw new EventProcessingException(errorMessage);
        }

        LOG.info("Message processing done!");
    }
}
