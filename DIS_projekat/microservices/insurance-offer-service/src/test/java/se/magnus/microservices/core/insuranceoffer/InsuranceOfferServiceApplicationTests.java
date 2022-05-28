package se.magnus.microservices.core.insuranceoffer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.magnus.api.core.insuranceOffer.*;
import se.magnus.microservices.core.insuranceoffer.persistence.*;

import static org.junit.Assert.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = { "spring.datasource.url=jdbc:h2:mem:review-db" })
public class InsuranceOfferServiceApplicationTests {

	@Autowired
	private WebTestClient client;

	@Autowired
	private InsuranceOfferRepository repository;

	@Before
	public void setupDb() {
		repository.deleteAll();
	}

	@Test
	public void getInsuranceOffersByInsuranceCompanyId() {

		int insuranceCompanyId = 1;

		assertEquals(0, repository.findByInsuranceCompanyId(insuranceCompanyId).size());

		postAndVerifyInsuranceOffer(insuranceCompanyId, 1, OK);
		postAndVerifyInsuranceOffer(insuranceCompanyId, 2, OK);
		postAndVerifyInsuranceOffer(insuranceCompanyId, 3, OK);

		assertEquals(3, repository.findByInsuranceCompanyId(insuranceCompanyId).size());
	}

	@Test
	public void postInsuranceOffer() {

		int insuranceCompanyId = 1;
		int insuranceOfferId = 1;

		postAndVerifyInsuranceOffer(insuranceCompanyId, insuranceOfferId, OK);
		assertNotNull(repository.findByInsuranceOfferId(insuranceOfferId));
	}

	@Test
	public void deleteInsuranceOffers() {

		int insuranceCompanyId = 1;
		int insuranceOfferId = 1;

		postAndVerifyInsuranceOffer(insuranceCompanyId, insuranceOfferId, OK);
		assertEquals(1, repository.findByInsuranceCompanyId(insuranceCompanyId).size());

		deleteAndVerifyInsuranceOfferByInsuranceCId(insuranceCompanyId, OK);
		assertEquals(0, repository.findByInsuranceCompanyId(insuranceCompanyId).size());

		deleteAndVerifyInsuranceOfferByInsuranceCId(insuranceCompanyId, OK);
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
	
	private WebTestClient.BodyContentSpec getAndVerifyInsuranceOffersByInsuranceCID(int insuranceCompanyId, HttpStatus expectedStatus) {
		return getAndVerifyInsuranceOffersByInsuranceCID("?insuranceCompanyId=" + insuranceCompanyId, expectedStatus);
	}

	private WebTestClient.BodyContentSpec getAndVerifyInsuranceOffersByInsuranceCID(String insuranceCompanyIdQuery, HttpStatus expectedStatus) {
		return client.get()
			.uri("/insuranceOffer" + insuranceCompanyIdQuery)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody();
	}

	private WebTestClient.BodyContentSpec postAndVerifyInsuranceOffer(int insuranceCompanyId, int insuranceOfferId,
			HttpStatus expectedStatus) {
		InsuranceOffer insuranceOffer = new InsuranceOffer(insuranceCompanyId, insuranceOfferId, "offername",
				"typeofferpr", "typeInsuranceCoverage", 582.75, "currencyOffer", "SA");
		return client.post().uri("/insuranceOffer").body(just(insuranceOffer), InsuranceOffer.class)
				.accept(APPLICATION_JSON).exchange().expectStatus().isEqualTo(expectedStatus).expectHeader()
				.contentType(APPLICATION_JSON).expectBody();
	}

	private WebTestClient.BodyContentSpec deleteAndVerifyInsuranceOfferByInsuranceCId(int insuranceCompanyId,
			HttpStatus expectedStatus) {
		return client.delete().uri("/insuranceOffer?insuranceCompanyId=" + insuranceCompanyId).accept(APPLICATION_JSON)
				.exchange().expectStatus().isEqualTo(expectedStatus).expectBody();
	}
}
