package se.magnus.microservices.core.insurancecompany;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.http.HttpStatus;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.magnus.api.core.insuranceCompany.InsuranceCompany;
import se.magnus.api.event.Event;
import se.magnus.util.exceptions.InvalidInputException;
import se.magnus.microservices.core.insurancecompany.persistence.InsuranceCompanyRepository;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;
import static se.magnus.api.event.Event.Type.CREATE;
import static se.magnus.api.event.Event.Type.DELETE;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = { "spring.data.mongodb.port: 0" })
public class InsuranceCompanyServiceApplicationTests {

	@Autowired
	private WebTestClient client;

	@Autowired
	private InsuranceCompanyRepository repository;
	
	@Autowired
	private Sink channels;

	private AbstractMessageChannel input = null;

	@Before
	public void setupDb() {
		input = (AbstractMessageChannel) channels.input();
		repository.deleteAll().block();
	}

	@Test
	public void getInsuranceCompanyById() {

		int insuranceCompanyId = 1;

		assertNull(repository.findByInsuranceCompanyId(insuranceCompanyId).block());
		assertEquals(0, (long)repository.count().block());

		sendCreateInsuranceCompanyEvent(insuranceCompanyId);

		assertNotNull(repository.findByInsuranceCompanyId(insuranceCompanyId).block());
		assertEquals(1, (long)repository.count().block());

		getAndVerifyInsuranceCompany(insuranceCompanyId, OK)
            .jsonPath("$.insuranceCompanyId").isEqualTo(insuranceCompanyId);
	}

	@Test
	public void deleteInsuranceCompany() {

		int insuranceCompanyId = 1;

		sendCreateInsuranceCompanyEvent(insuranceCompanyId);
		assertNotNull(repository.findByInsuranceCompanyId(insuranceCompanyId).block());

		sendDeleteInsuranceCompanyEvent(insuranceCompanyId);
		assertNull(repository.findByInsuranceCompanyId(insuranceCompanyId).block());

		sendDeleteInsuranceCompanyEvent(insuranceCompanyId);
		
	}

	@Test
	public void getInsuranceCompanyInvalidParameter() {

		getAndVerifyInsuranceCompany("/invalid-param-type", BAD_REQUEST).jsonPath("$.path")
				.isEqualTo("/insuranceCompany/invalid-param-type").jsonPath("$.message").isEqualTo("Type mismatch.");
	}

	@Test
	public void getInsuranceCompanyNotFound() {

		int insuranceCompanyIdNotFound = 13;

		getAndVerifyInsuranceCompany(String.valueOf(insuranceCompanyIdNotFound), NOT_FOUND).jsonPath("$.path")
				.isEqualTo("/insuranceCompany/" + insuranceCompanyIdNotFound).jsonPath("$.message")
				.isEqualTo("No insurance company found for insuranceCompanyId: " + insuranceCompanyIdNotFound);
	}

	@Test
	public void getInsuranceCompanyInvalidParameterNegativeValue() {

		int insuranceCompanyIdInvalid = -1;

		getAndVerifyInsuranceCompany(String.valueOf(insuranceCompanyIdInvalid), UNPROCESSABLE_ENTITY).jsonPath("$.path")
				.isEqualTo("/insuranceCompany/" + insuranceCompanyIdInvalid).jsonPath("$.message")
				.isEqualTo("Invalid insuranceCompanyId: " + insuranceCompanyIdInvalid);
	}
	
	private WebTestClient.BodyContentSpec getAndVerifyInsuranceCompany(int insuranceCompanyId, HttpStatus expectedStatus) {
		return getAndVerifyInsuranceCompany("/" + insuranceCompanyId, expectedStatus);
	}

	private WebTestClient.BodyContentSpec getAndVerifyInsuranceCompany(String insuranceCompanyIdPath,
			HttpStatus expectedStatus) {
		return client.get().uri("/insuranceCompany/" + insuranceCompanyIdPath).accept(APPLICATION_JSON).exchange()
				.expectStatus().isEqualTo(expectedStatus).expectHeader().contentType(APPLICATION_JSON).expectBody();
	}
	
	private void sendCreateInsuranceCompanyEvent(int insuranceCompanyId) {
		InsuranceCompany insuranceCompany = new InsuranceCompany(insuranceCompanyId, "Name " + insuranceCompanyId, "City " + insuranceCompanyId, "Address " + insuranceCompanyId, "PhoneNumber " + insuranceCompanyId, "SA");
		Event<Integer, InsuranceCompany> event = new Event(CREATE, insuranceCompanyId, insuranceCompany);
		input.send(new GenericMessage<>(event));
	}

	private void sendDeleteInsuranceCompanyEvent(int insuranceCompanyId) {
		Event<Integer, InsuranceCompany> event = new Event(DELETE, insuranceCompanyId, null);
		input.send(new GenericMessage<>(event));
	}

}
