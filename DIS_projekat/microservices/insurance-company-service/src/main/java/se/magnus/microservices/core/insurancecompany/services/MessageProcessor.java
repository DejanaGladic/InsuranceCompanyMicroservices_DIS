package se.magnus.microservices.core.insurancecompany.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import se.magnus.api.core.insuranceCompany.*;
import se.magnus.api.event.Event;
import se.magnus.util.exceptions.EventProcessingException;

@EnableBinding(Sink.class)
public class MessageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);

    private final InsuranceCompanyService insuranceCompanyService;

    @Autowired
    public MessageProcessor (InsuranceCompanyService insuranceCompanyService){
        this.insuranceCompanyService = insuranceCompanyService;
    }

    @StreamListener(target = Sink.INPUT)
    public void process(Event<Integer, InsuranceCompany> event) {

        LOG.info("Process message created at {}...", event.getEventCreatedAt());

        switch (event.getEventType()) {

        case CREATE:
            InsuranceCompany insuranceCompany = event.getData();
            LOG.info("Create insuranceCompany with ID: {}", insuranceCompany.getInsuranceCompanyId());
            insuranceCompanyService.createInsuranceCompany(insuranceCompany);
            break;

        case DELETE:
            int insuranceCompanyId = event.getKey();
            LOG.info("Delete insurance companies with insuranceCompanyId: {}", insuranceCompanyId);
            insuranceCompanyService.deleteInsuranceCompany(insuranceCompanyId);
            break;

        default:
            String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
            LOG.warn(errorMessage);
            throw new EventProcessingException(errorMessage);
        }

        LOG.info("Message processing done!");
    }
}