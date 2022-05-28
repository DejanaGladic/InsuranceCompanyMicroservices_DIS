package se.magnus.microservices.core.transaction;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.magnus.api.core.transaction.*;
import se.magnus.microservices.core.transaction.persistence.*;
import java.util.Date;

import static org.junit.Assert.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = { "spring.datasource.url=jdbc:h2:mem:review-db" })
public class TransactionServiceApplicationTests {

	@Autowired
	private WebTestClient client;
	
	@Autowired
	private TransactionRepository repository;

	@Before
	public void setupDb() {
		repository.deleteAll();
	}

	@Test
	public void getTransactionsByInsuranceCompanyId() {

		int insuranceCompanyId = 1;

		assertEquals(0, repository.findByInsuranceCompanyId(insuranceCompanyId).size());

		postAndVerifyTransaction(insuranceCompanyId, 1, OK);
		postAndVerifyTransaction(insuranceCompanyId, 2, OK);
		postAndVerifyTransaction(insuranceCompanyId, 3, OK);

		assertEquals(3, repository.findByInsuranceCompanyId(insuranceCompanyId).size());
	}

	@Test
	public void postTransaction() {

		int insuranceCompanyId = 1;
		int transactionId = 1;

		postAndVerifyTransaction(insuranceCompanyId, transactionId, OK);
		assertNotNull(repository.findByTransactionId(transactionId));
	}

	@Test
	public void deleteTransactions() {

		int insuranceCompanyId = 1;
		int transactionId = 1;

		postAndVerifyTransaction(insuranceCompanyId, transactionId, OK);
		assertEquals(1, repository.findByInsuranceCompanyId(insuranceCompanyId).size());

		deleteAndVerifyTransactionsByInsuranceCId(insuranceCompanyId, OK);
		assertEquals(0, repository.findByInsuranceCompanyId(insuranceCompanyId).size());

		deleteAndVerifyTransactionsByInsuranceCId(insuranceCompanyId, OK);
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

	private WebTestClient.BodyContentSpec postAndVerifyTransaction(int insuranceCompanyId, int transactionId,
			HttpStatus expectedStatus) {
		Transaction transaction = new Transaction(insuranceCompanyId, transactionId, "typeTransaction",
				new Date(), 582.55, "currencyTransaction", "accountNumber", "policyNumber","SA");
		return client.post().uri("/transaction").body(just(transaction), Transaction.class)
				.accept(APPLICATION_JSON).exchange().expectStatus().isEqualTo(expectedStatus).expectHeader()
				.contentType(APPLICATION_JSON).expectBody();
	}

	private WebTestClient.BodyContentSpec deleteAndVerifyTransactionsByInsuranceCId(int insuranceCompanyId,
			HttpStatus expectedStatus) {
		return client.delete().uri("/transaction?insuranceCompanyId=" + insuranceCompanyId).accept(APPLICATION_JSON)
				.exchange().expectStatus().isEqualTo(expectedStatus).expectBody();
	}
}
