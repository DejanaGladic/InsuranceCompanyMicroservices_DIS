package se.magnus.microservices.core.insuranceoffer;

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
import se.magnus.api.core.insuranceOffer.*;
import se.magnus.api.event.Event;
import se.magnus.microservices.core.insuranceoffer.persistence.*;

import static org.junit.Assert.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;
import static se.magnus.api.event.Event.Type.CREATE;
import static se.magnus.api.event.Event.Type.DELETE;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = { "eureka.client.enabled=false","spring.datasource.url=jdbc:h2:mem:insurance-offer-db", "spring.cloud.config.enabled=false" })
public class InsuranceOfferServiceApplicationTests {

	@Autowired
	private WebTestClient client;

	@Autowired
	private InsuranceOfferRepository repository;

	@Autowired
	private Sink channels;

	private AbstractMessageChannel input = null;

	@Before
	public void setupDb() {
		input = (AbstractMessageChannel) channels.input();
		repository.deleteAll();
	}

	@Test
	public void getInsuranceOffersByInsuranceCompanyId() {

		int insuranceCompanyId = 1;

		assertEquals(0, repository.findByInsuranceCompanyId(insuranceCompanyId).size());

		sendCreateInsuranceOfferEvent(insuranceCompanyId, 1);
		sendCreateInsuranceOfferEvent(insuranceCompanyId, 2);
		sendCreateInsuranceOfferEvent(insuranceCompanyId, 3);

		assertEquals(3, repository.findByInsuranceCompanyId(insuranceCompanyId).size());

		getAndVerifyInsuranceOffersByInsuranceCID("?insuranceCompanyId="+ insuranceCompanyId, OK)
				.jsonPath("$.length()").isEqualTo(3)
				.jsonPath("$[2].insuranceCompanyId").isEqualTo(insuranceCompanyId)
				.jsonPath("$[2].insuranceOfferId").isEqualTo(3);
	}

	@Test
	public void deleteInsuranceOffers() {

		int insuranceCompanyId = 1;
		int insuranceOfferId = 1;

		sendCreateInsuranceOfferEvent(insuranceCompanyId, insuranceOfferId);
		assertEquals(1, repository.findByInsuranceCompanyId(insuranceCompanyId).size());

		sendDeleteInsuranceOfferEvent(insuranceCompanyId);
		assertEquals(0, repository.findByInsuranceCompanyId(insuranceCompanyId).size());

		sendDeleteInsuranceOfferEvent(insuranceCompanyId);
	}

	@Test
	public void getInsuranceOffersMissingParameter() {
		getAndVerifyInsuranceOffersByInsuranceCID("", BAD_REQUEST).jsonPath("$.path").isEqualTo("/insuranceOffer")
				.jsonPath("$.message").isEqualTo("Required int parameter 'insuranceCompanyId' is not present");
	}

	@Test
	public void getInsuranceOffersInvalidParameter() {

		getAndVerifyInsuranceOffersByInsuranceCID("?insuranceCompanyId=no-integer", BAD_REQUEST).jsonPath("$.path")
				.isEqualTo("/insuranceOffer").jsonPath("$.message").isEqualTo("Type mismatch.");
	}

	@Test
	public void getInsuranceOffersNotFound() {

		getAndVerifyInsuranceOffersByInsuranceCID("?insuranceCompanyId=213", OK).jsonPath("$.length()").isEqualTo(0);
	}

	@Test
	public void getInsuranceOffersInvalidParameterNegativeValue() {

		int insuranceCompanyIdInvalid = -1;

		getAndVerifyInsuranceOffersByInsuranceCID("?insuranceCompanyId=" + insuranceCompanyIdInvalid,
				UNPROCESSABLE_ENTITY).jsonPath("$.path").isEqualTo("/insuranceOffer").jsonPath("$.message")
						.isEqualTo("Invalid insuranceCompanyId: " + insuranceCompanyIdInvalid);
	}
	
	/*private WebTestClient.BodyContentSpec getAndVerifyInsuranceOffersByInsuranceCID(int insuranceCompanyId, HttpStatus expectedStatus) {
		return getAndVerifyInsuranceOffersByInsuranceCID("?insuranceCompanyId=" + insuranceCompanyId, expectedStatus);
	}*/

	private WebTestClient.BodyContentSpec getAndVerifyInsuranceOffersByInsuranceCID(String insuranceCompanyIdQuery, HttpStatus expectedStatus) {
		return client.get()
			.uri("/insuranceOffer" + insuranceCompanyIdQuery)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody();
	}

	private void sendCreateInsuranceOfferEvent(int insuranceCompanyId, int insuranceOfferId) {
		InsuranceOffer insuranceOffer = new InsuranceOffer(insuranceCompanyId, insuranceOfferId, "OfferName " + insuranceOfferId,null, null, 505.52,"CurrencyOffer " + insuranceOfferId, "SA");
		Event<Integer, InsuranceCompany> event = new Event(CREATE, insuranceCompanyId, insuranceOffer);
		input.send(new GenericMessage<>(event));
	}

	private void sendDeleteInsuranceOfferEvent(int insuranceCompanyId) {
		Event<Integer, InsuranceCompany> event = new Event(DELETE, insuranceCompanyId, null);
		input.send(new GenericMessage<>(event));
	}
}
