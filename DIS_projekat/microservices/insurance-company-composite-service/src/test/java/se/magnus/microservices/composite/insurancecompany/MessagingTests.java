package se.magnus.microservices.composite.insurancecompany;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.magnus.api.composite.insuranceCompany.EmployeeSummary;
import se.magnus.api.composite.insuranceCompany.InsuranceCompanyAggregate;
import se.magnus.api.composite.insuranceCompany.InsuranceOfferSummary;
import se.magnus.api.composite.insuranceCompany.TransactionSummary;
import se.magnus.api.core.employee.Employee;
import se.magnus.api.core.insuranceCompany.InsuranceCompany;
import se.magnus.api.core.insuranceOffer.InsuranceOffer;
import se.magnus.api.core.transaction.Transaction;
import se.magnus.api.event.Event;
import se.magnus.microservices.composite.insurancecompany.services.InsuranceCompanyCompositeIntegration;

import java.util.concurrent.BlockingQueue;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.cloud.stream.test.matcher.MessageQueueMatcher.receivesPayloadThat;
import static org.springframework.http.HttpStatus.OK;
import static reactor.core.publisher.Mono.just;
import static se.magnus.api.event.Event.Type.CREATE;
import static se.magnus.api.event.Event.Type.DELETE;
import static se.magnus.microservices.composite.insurancecompany.IsSameEvent.sameEventExceptCreatedAt;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=RANDOM_PORT)
public class MessagingTests {

    private static final int INSURANCE_COMPANY_ID_OK = 1;
    private static final int INSURANCE_COMPANY_ID_NOT_FOUND = 2;
    private static final int INSURANCE_COMPANY_ID_INVALID = 3;

    @Autowired
    private WebTestClient client;

    @Autowired
    private InsuranceCompanyCompositeIntegration.MessageSources channels;

    @Autowired
    private MessageCollector collector;

    BlockingQueue<Message<?>> queueInsuranceCompany = null;
    BlockingQueue<Message<?>> queueEmployees = null;

    BlockingQueue<Message<?>> queueInsuranceOffers = null;
    BlockingQueue<Message<?>> queueTransactions = null;

    @Before
    public void setUp() {
        queueInsuranceCompany = getQueue(channels.outputInsuranceCompanies());
        queueEmployees = getQueue(channels.outputEmployees());
        queueInsuranceOffers = getQueue(channels.outputInsuranceOffers());
        queueTransactions = getQueue(channels.outputTransactions());
    }

    @Test
    public void createCompositeInsuranceCompany1() {

        InsuranceCompanyAggregate composite = new InsuranceCompanyAggregate(1, "name", "city", "address","phoneNumber", null, null, null, null);
        postAndVerifyInsuranceCompany(composite, OK);

        // Assert one expected new product events queued up
        assertEquals(1, queueInsuranceCompany.size());

        Event<Integer, InsuranceCompany> expectedEvent = new Event(CREATE, composite.getInsuranceCompanyId(), new InsuranceCompany(composite.getInsuranceCompanyId(), composite.getName(), composite.getCity(), composite.getAddress(), composite.getPhoneNumber(), null));
        assertThat(queueInsuranceCompany, is(receivesPayloadThat(sameEventExceptCreatedAt(expectedEvent))));

        // Assert none recommendations and review events
        assertEquals(0, queueEmployees.size());
        assertEquals(0, queueInsuranceOffers.size());
        assertEquals(0, queueTransactions.size());
    }

    @Test
    public void createCompositeInsuranceCompany2() {

        InsuranceCompanyAggregate composite = new InsuranceCompanyAggregate(1, "name", "city", "address","phoneNumber",
                singletonList(new EmployeeSummary(1, "name employee", "surname employee", "specialization")),
                singletonList(new InsuranceOfferSummary(1, "offerName", 505.00, "currencyOffer")),
                singletonList(new TransactionSummary(1,"typeTrans", 5050.50,"currency", "policyNumber")), null);

        postAndVerifyInsuranceCompany(composite, OK);

        // Assert one create insurance company event queued up
        assertEquals(1, queueInsuranceCompany.size());

        Event<Integer, InsuranceCompany> expectedProductEvent = new Event(CREATE, composite.getInsuranceCompanyId(), new InsuranceCompany(composite.getInsuranceCompanyId(), composite.getName(), composite.getCity(), composite.getAddress(), composite.getPhoneNumber(), null));
        assertThat(queueInsuranceCompany, receivesPayloadThat(sameEventExceptCreatedAt(expectedProductEvent)));

        // Assert one create employee event queued up
        assertEquals(1, queueEmployees.size());

        EmployeeSummary emp = composite.getEmployees().get(0);
        Event<Integer, InsuranceCompany> expectedEmployeeEvent = new Event(CREATE, composite.getInsuranceCompanyId(), new Employee(composite.getInsuranceCompanyId(), emp.getEmployeeId(), emp.getName(), emp.getSurname(),null,emp.getSpecialization(), null));
        assertThat(queueEmployees, receivesPayloadThat(sameEventExceptCreatedAt(expectedEmployeeEvent)));

        // Assert one create insurance offers event queued up
        assertEquals(1, queueInsuranceOffers.size());

        InsuranceOfferSummary insOff = composite.getInsuranceOffers().get(0);
        Event<Integer, InsuranceCompany> expectedInsuranceOfferEvent = new Event(CREATE, composite.getInsuranceCompanyId(), new InsuranceOffer(composite.getInsuranceCompanyId(), insOff.getInsuranceOfferId(), insOff.getOfferName(), null,null, insOff.getPrice(), insOff.getCurrencyOffer(), null));
        assertThat(queueInsuranceOffers, receivesPayloadThat(sameEventExceptCreatedAt(expectedInsuranceOfferEvent)));

        // Assert one create transaction event queued up
        assertEquals(1, queueTransactions.size());

        TransactionSummary tran = composite.getTransactions().get(0);
        Event<Integer, InsuranceCompany> expectedTransactionEvent = new Event(CREATE, composite.getInsuranceCompanyId(), new Transaction(composite.getInsuranceCompanyId(), tran.getTransactionId(), tran.getTypeTransaction(), null, tran.getAmount(),tran.getCurrencyTransaction(), null, tran.getPolicyNumber(), null));
        assertThat(queueTransactions, receivesPayloadThat(sameEventExceptCreatedAt(expectedTransactionEvent)));
    }

    @Test
    public void deleteCompositeInsuranceCompany() {

        deleteAndVerifyInsuranceCompany(1, OK);

        // Assert one delete product InsuranceCompany queued up
        assertEquals(1, queueInsuranceCompany.size());

        Event<Integer, InsuranceCompany> expectedEvent = new Event(DELETE, 1, null);
        assertThat(queueInsuranceCompany, is(receivesPayloadThat(sameEventExceptCreatedAt(expectedEvent))));

        // Assert one delete Employee event queued up
        assertEquals(1, queueEmployees.size());

        Event<Integer, InsuranceCompany> expectedEmployeeEvent = new Event(DELETE, 1, null);
        assertThat(queueEmployees, receivesPayloadThat(sameEventExceptCreatedAt(expectedEmployeeEvent)));

        // Assert one delete Insurance Offer event queued up
        assertEquals(1, queueInsuranceOffers.size());

        Event<Integer, InsuranceCompany> expectedInsuranceOfferEvent = new Event(DELETE, 1, null);
        assertThat(queueInsuranceOffers, receivesPayloadThat(sameEventExceptCreatedAt(expectedInsuranceOfferEvent)));

        // Assert one delete Transaction event queued up
        assertEquals(1, queueTransactions.size());

        Event<Integer, InsuranceCompany> expectedTransactionEvent = new Event(DELETE, 1, null);
        assertThat(queueTransactions, receivesPayloadThat(sameEventExceptCreatedAt(expectedTransactionEvent)));
    }

    private BlockingQueue<Message<?>> getQueue(MessageChannel messageChannel) {
        return collector.forChannel(messageChannel);
    }

    private void postAndVerifyInsuranceCompany(InsuranceCompanyAggregate compositeInsuranceCompany, HttpStatus expectedStatus) {
        client.post()
                .uri("/insurance-company-composite")
                .body(just(compositeInsuranceCompany), InsuranceCompanyAggregate.class)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus);
    }

    private void deleteAndVerifyInsuranceCompany(int insuranceCompanyId, HttpStatus expectedStatus) {
        client.delete()
                .uri("/insurance-company-composite/" + insuranceCompanyId)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus);
    }
}
