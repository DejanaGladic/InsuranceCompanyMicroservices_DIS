package se.magnus.microservices.core.insurancecompany;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.magnus.api.core.insuranceCompany.InsuranceCompany;
import se.magnus.microservices.core.insurancecompany.persistence.InsuranceCompanyRepository;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = { "spring.data.mongodb.port: 0" })
public class InsuranceCompanyServiceApplicationTests {

	@Autowired
	private WebTestClient client;

	@Autowired
	private InsuranceCompanyRepository repository;

	@Before
	public void setupDb() {
		repository.deleteAll();
	}

	@Test
	public void getInsuranceCompanyById() {

		int insuranceCompanyId = 1;

		postAndVerifyInsuranceCompany(insuranceCompanyId, OK);

		assertTrue(repository.findByInsuranceCompanyId(insuranceCompanyId).isPresent());
	}

	@Test
	public void postInsuranceCompany() {

		int insuranceCompanyId = 1;
		postAndVerifyInsuranceCompany(insuranceCompanyId, OK);
		assertTrue(repository.findByInsuranceCompanyId(insuranceCompanyId).isPresent());
	}

	@Test
	public void deleteInsuranceCompany() {

		int insuranceCompanyId = 1;

		postAndVerifyInsuranceCompany(insuranceCompanyId, OK);
		assertTrue(repository.findByInsuranceCompanyId(insuranceCompanyId).isPresent());

		deleteAndVerifyInsuranceCompany(insuranceCompanyId, OK);
		assertFalse(repository.findByInsuranceCompanyId(insuranceCompanyId).isPresent());

		deleteAndVerifyInsuranceCompany(insuranceCompanyId, OK);
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

	private WebTestClient.BodyContentSpec postAndVerifyInsuranceCompany(int insuranceCompanyId,
			HttpStatus expectedStatus) {

		InsuranceCompany insuranceCompany = new InsuranceCompany(insuranceCompanyId, "insuranceCompany 1", "city 1",
				"address 1", "phone number 1", "SA");

		return client.post().uri("/insuranceCompany").body(just(insuranceCompany), InsuranceCompany.class)
				.accept(APPLICATION_JSON).exchange().expectStatus().isEqualTo(expectedStatus).expectHeader()
				.contentType(APPLICATION_JSON).expectBody();
	}

	private WebTestClient.BodyContentSpec deleteAndVerifyInsuranceCompany(int insuranceCompanyId,
			HttpStatus expectedStatus) {
		return client.delete().uri("/insuranceCompany/" + insuranceCompanyId).accept(APPLICATION_JSON).exchange()
				.expectStatus().isEqualTo(expectedStatus).expectBody();
	}

}
