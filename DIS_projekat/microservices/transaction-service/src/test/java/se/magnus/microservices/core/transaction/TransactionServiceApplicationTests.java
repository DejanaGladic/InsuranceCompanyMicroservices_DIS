package se.magnus.microservices.core.transaction;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.http.HttpStatus;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.magnus.api.core.insuranceCompany.InsuranceCompany;
import se.magnus.api.core.transaction.*;
import se.magnus.api.event.Event;
import se.magnus.microservices.core.transaction.persistence.*;
import java.util.Date;

import static org.junit.Assert.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;
import static se.magnus.api.event.Event.Type.CREATE;
import static se.magnus.api.event.Event.Type.DELETE;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=RANDOM_PORT, properties = {
		"logging.level.se.magnus=DEBUG",
		"eureka.client.enabled=false",
		"spring.cloud.config.enabled=false",
		"spring.datasource.url=jdbc:h2:mem:transaction-db",
		"server.error.include-message=always"})
public class TransactionServiceApplicationTests {

	@Autowired
	private WebTestClient client;
	
	@Autowired
	private TransactionRepository repository;

	@Autowired
	private Sink channels;

	private AbstractMessageChannel input = null;

	@Before
	public void setupDb() {
		input = (AbstractMessageChannel) channels.input();
		repository.deleteAll();
	}

	@Test
	public void getTransactionsByInsuranceCompanyId() {

		int insuranceCompanyId = 1;

		assertEquals(0, repository.findByInsuranceCompanyId(insuranceCompanyId).size());

		sendCreateTransactionEvent(insuranceCompanyId, 1);
		sendCreateTransactionEvent(insuranceCompanyId, 2);
		sendCreateTransactionEvent(insuranceCompanyId, 3);

		assertEquals(3, repository.findByInsuranceCompanyId(insuranceCompanyId).size());

		getAndVerifyTransactionsByInsuranceCID(insuranceCompanyId, OK)
				.jsonPath("$.length()").isEqualTo(3)
				.jsonPath("$[2].insuranceCompanyId").isEqualTo(insuranceCompanyId)
				.jsonPath("$[2].transactionId").isEqualTo(3);
	}

	@Test
	public void deleteTransactions() {

		int insuranceCompanyId = 1;
		int transactionId = 1;

		sendCreateTransactionEvent(insuranceCompanyId, transactionId);
		assertEquals(1, repository.findByInsuranceCompanyId(insuranceCompanyId).size());

		sendDeleteTransactionEvent(insuranceCompanyId);
		assertEquals(0, repository.findByInsuranceCompanyId(insuranceCompanyId).size());

		sendDeleteTransactionEvent(insuranceCompanyId);
	}

	@Test
	public void getTransactionsMissingParameter() {
		getAndVerifyTransactionsByInsuranceCID("", BAD_REQUEST).jsonPath("$.path").isEqualTo("/transaction")
				.jsonPath("$.message").isEqualTo("Required int parameter 'insuranceCompanyId' is not present");
	}

	@Test
	public void getTransactionsInvalidParameter() {

		getAndVerifyTransactionsByInsuranceCID("?insuranceCompanyId=no-integer", BAD_REQUEST).jsonPath("$.path")
				.isEqualTo("/transaction").jsonPath("$.message").isEqualTo("Type mismatch.");
	}

	@Test
	public void getTransactionsNotFound() {

		getAndVerifyTransactionsByInsuranceCID("?insuranceCompanyId=213", OK).jsonPath("$.length()").isEqualTo(0);
	}

	@Test
	public void getTransactionsInvalidParameterNegativeValue() {

		int insuranceCompanyIdInvalid = -1;

		getAndVerifyTransactionsByInsuranceCID("?insuranceCompanyId=" + insuranceCompanyIdInvalid,
				UNPROCESSABLE_ENTITY).jsonPath("$.path").isEqualTo("/transaction").jsonPath("$.message")
						.isEqualTo("Invalid insuranceCompanyId: " + insuranceCompanyIdInvalid);
	}
	
	private WebTestClient.BodyContentSpec getAndVerifyTransactionsByInsuranceCID(int insuranceCompanyId, HttpStatus expectedStatus) {
		return getAndVerifyTransactionsByInsuranceCID("?insuranceCompanyId=" + insuranceCompanyId, expectedStatus);
	}

	private WebTestClient.BodyContentSpec getAndVerifyTransactionsByInsuranceCID(String insuranceCompanyIdQuery, HttpStatus expectedStatus) {
		return client.get()
			.uri("/transaction" + insuranceCompanyIdQuery)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody();
	}

	private void sendCreateTransactionEvent(int insuranceCompanyId, int transactionId) {
		Transaction transaction = new Transaction(insuranceCompanyId, transactionId,"TypeTransaction"+transactionId, null, 505.65,"CurrencyTransaction"+transactionId, null,"Policy Number"+transactionId,"SA");
		Event<Integer, InsuranceCompany> event = new Event(CREATE, insuranceCompanyId, transaction);
		input.send(new GenericMessage<>(event));
	}

	private void sendDeleteTransactionEvent(int insuranceCompanyId) {
		Event<Integer, InsuranceCompany> event = new Event(DELETE, insuranceCompanyId, null);
		input.send(new GenericMessage<>(event));
	}
}
