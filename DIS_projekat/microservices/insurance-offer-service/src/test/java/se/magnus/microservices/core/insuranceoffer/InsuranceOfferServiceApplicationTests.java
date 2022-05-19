package se.magnus.microservices.core.insuranceoffer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=RANDOM_PORT)
public class InsuranceOfferServiceApplicationTests {

	@Autowired
	private WebTestClient client;

	@Test
	public void getInsuranceOffersByInsuranceCompanyId() {

		int insuranceCompanyId = 1;

		client.get()
			.uri("/insuranceOffer?insuranceCompanyId=" + insuranceCompanyId)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.length()").isEqualTo(3)
			.jsonPath("$[0].insuranceCompanyId").isEqualTo(insuranceCompanyId);
	}

	@Test
	public void getInsuranceOffersMissingParameter() {

		client.get()
			.uri("/insuranceOffer")
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(BAD_REQUEST)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.path").isEqualTo("/insuranceOffer")
			.jsonPath("$.message").isEqualTo("Required int parameter 'insuranceCompanyId' is not present");
	}

	@Test
	public void getInsuranceOffersInvalidParameter() {

		client.get()
			.uri("/insuranceOffer?insuranceCompanyId=no-integer")
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(BAD_REQUEST)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.path").isEqualTo("/insuranceOffer")
			.jsonPath("$.message").isEqualTo("Type mismatch.");
	}

	@Test
	public void  getInsuranceOffersNotFound() {

		int insuranceCompanyIdNotFound = 113;

		client.get()
			.uri("/insuranceOffer?insuranceCompanyId=" + insuranceCompanyIdNotFound)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.length()").isEqualTo(0);
	}

	@Test
	public void getInsuranceOffersInvalidParameterNegativeValue() {

		int insuranceCompanyIdInvalid = -1;

		client.get()
			.uri("/insuranceOffer?insuranceCompanyId=" + insuranceCompanyIdInvalid)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.path").isEqualTo("/insuranceOffer")
			.jsonPath("$.message").isEqualTo("Invalid insuranceCompanyId: " + insuranceCompanyIdInvalid);
	}
}
