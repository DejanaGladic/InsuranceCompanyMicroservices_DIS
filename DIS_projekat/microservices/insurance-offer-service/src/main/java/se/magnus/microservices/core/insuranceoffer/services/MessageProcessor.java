package se.magnus.microservices.core.insuranceoffer.services;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import se.magnus.api.core.insuranceOffer.InsuranceOffer;
import se.magnus.api.core.insuranceOffer.InsuranceOfferService;
import se.magnus.api.event.Event;
import se.magnus.util.exceptions.EventProcessingException;

@EnableBinding(Sink.class)
public class MessageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);

    private final InsuranceOfferService insuranceOfferService;

    @Autowired
    public MessageProcessor (InsuranceOfferService insuranceOfferService){
        this.insuranceOfferService = insuranceOfferService;
    }

    @StreamListener(target = Sink.INPUT)
    public void process(Event<Integer, InsuranceOffer> event) {

        LOG.info("Process message created at {}...", event.getEventCreatedAt());

        switch (event.getEventType()) {

            case CREATE:
                InsuranceOffer insuranceOffer = event.getData();
                LOG.info("Create insuranceOffer with ID: {}", insuranceOffer.getInsuranceOfferId());
                insuranceOfferService.createInsuranceOffer(insuranceOffer);
                break;

            case DELETE:
                int insuranceCompanyId = event.getKey();
                LOG.info("Delete insurance offers with insuranceCompanyId: {}", insuranceCompanyId);
                insuranceOfferService.deleteInsuranceOffers(insuranceCompanyId);
                break;

            default:
                String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
                LOG.warn(errorMessage);
                throw new EventProcessingException(errorMessage);
        }

        LOG.info("Message processing done!");
    }
}
