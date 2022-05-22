package se.magnus.microservices.core.insurancecompany;

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
public class InsuranceCompanyServiceApplicationTests {

	@Autowired
	private WebTestClient client;

	@Test
	public void getInsuranceCompany() {

		int insuranceCompanyId = 1;

		client.get()
			.uri("/insuranceCompany/" + insuranceCompanyId)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.insuranceCompanyId").isEqualTo(insuranceCompanyId);
	}

	@Test
	public void getInsuranceCompanyInvalidParameter() {
		
		client.get()
			.uri("/insuranceCompany/invalid-param-type")
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(BAD_REQUEST)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.path").isEqualTo("/insuranceCompany/invalid-param-type");	
	}

	@Test
	public void  getInsuranceCompanyNotFound() {

		int insuranceCompanyIdNotFound = 13;

		client.get()
			.uri("/insuranceCompany/" + insuranceCompanyIdNotFound)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isNotFound()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
            .jsonPath("$.path").isEqualTo("/insuranceCompany/" + insuranceCompanyIdNotFound)
            .jsonPath("$.message").isEqualTo("No insurance company found for insuranceCompanyId: " + insuranceCompanyIdNotFound);
	}

	@Test
	public void getInsuranceCompanyInvalidParameterNegativeValue() {

		int insuranceCompanyIdInvalid = -1;

		client.get()
			.uri("/insuranceCompany/" + insuranceCompanyIdInvalid)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.path").isEqualTo("/insuranceCompany/" + insuranceCompanyIdInvalid)
			.jsonPath("$.message").isEqualTo("Invalid insuranceCompanyId: " + insuranceCompanyIdInvalid);
	}
}
