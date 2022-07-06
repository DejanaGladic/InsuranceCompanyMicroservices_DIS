package se.magnus.microservices.composite.insurancecompany;

import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.magnus.api.composite.insuranceCompany.*;
import se.magnus.api.core.insuranceCompany.*;
import se.magnus.api.core.employee.*;
import se.magnus.api.core.insuranceOffer.*;
import se.magnus.api.core.transaction.*;
import se.magnus.microservices.composite.insurancecompany.services.*;
import se.magnus.util.exceptions.InvalidInputException;
import se.magnus.util.exceptions.NotFoundException;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT,classes = {InsuranceCompanyCompositeServiceApplication.class, TestSecurityConfig.class},
		properties = {"spring.main.allow-bean-definition-overriding=true","eureka.client.enabled=false","spring.cloud.config.enabled=false"})
public class InsuranceCompanyCompositeServiceApplicationTests {

	private static final int INSURANCE_COMPANY_ID_OK = 1;
	private static final int INSURANCE_COMPANY_ID_NOT_FOUND = 13;
	private static final int INSURANCE_COMPANY_ID_INVALID = -1;

	@Autowired
	private WebTestClient client;

	@MockBean
	private InsuranceCompanyCompositeIntegration compositeIntegration;

	@Before
	public void setUp() {

		when(compositeIntegration.getInsuranceCompany(INSURANCE_COMPANY_ID_OK)).
			thenReturn(Mono.just(new InsuranceCompany(INSURANCE_COMPANY_ID_OK, "name","city",
					"address", "phoneNumber", "mock-address")));
		
		when(compositeIntegration.getEmployees(INSURANCE_COMPANY_ID_OK)).
			thenReturn(Flux.fromIterable(singletonList(new Employee(INSURANCE_COMPANY_ID_OK, 1,
					"Dejana","Gladic", "Education 1", "Specialization 1", "mock-address"))));
		
		when(compositeIntegration.getInsuranceOffers(INSURANCE_COMPANY_ID_OK)).
			thenReturn(Flux.fromIterable(singletonList(new InsuranceOffer(INSURANCE_COMPANY_ID_OK, 1, "OfferName 1",
					"typeOfferProgram 1", "typeInsuranceCoverage 1",570.67, "currencyOffer 1","mock-address"))));

		when(compositeIntegration.getTransactions(INSURANCE_COMPANY_ID_OK)).
			thenReturn(Flux.fromIterable(singletonList(new Transaction(INSURANCE_COMPANY_ID_OK, 1, "typeTransaction 1",new Date(),
					570.67, "currencyTransaction 1", "accountNumber 1","policeNumber 1", "mock-address"))));
			
		when(compositeIntegration.getInsuranceCompany(INSURANCE_COMPANY_ID_NOT_FOUND)).thenThrow(new NotFoundException("NOT FOUND: " + INSURANCE_COMPANY_ID_NOT_FOUND));

		when(compositeIntegration.getInsuranceCompany(INSURANCE_COMPANY_ID_INVALID)).thenThrow(new InvalidInputException("INVALID: " + INSURANCE_COMPANY_ID_INVALID));
	}

	@Test
	public void contextLoads() {
	}

	@Test
	public void getInsuranceCompanyById() {
		getAndVerifyInsuranceCompany(INSURANCE_COMPANY_ID_OK, OK)
        .jsonPath("$.insuranceCompanyId").isEqualTo(INSURANCE_COMPANY_ID_OK)
        .jsonPath("$.employees.length()").isEqualTo(1)
        .jsonPath("$.insuranceOffers.length()").isEqualTo(1)
        .jsonPath("$.transactions.length()").isEqualTo(1);
	}

	@Test
	public void getInsuranceCompanyNotFound() {
		getAndVerifyInsuranceCompany(INSURANCE_COMPANY_ID_NOT_FOUND, NOT_FOUND)
        .jsonPath("$.path").isEqualTo("/insurance-company-composite/" + INSURANCE_COMPANY_ID_NOT_FOUND)
        .jsonPath("$.message").isEqualTo("NOT FOUND: " + INSURANCE_COMPANY_ID_NOT_FOUND);
		
	}

	@Test
	public void getInsuranceCompanyInvalidInput() {
		getAndVerifyInsuranceCompany(INSURANCE_COMPANY_ID_INVALID, UNPROCESSABLE_ENTITY)
        .jsonPath("$.path").isEqualTo("/insurance-company-composite/" + INSURANCE_COMPANY_ID_INVALID)
        .jsonPath("$.message").isEqualTo("INVALID: " + INSURANCE_COMPANY_ID_INVALID);
		
	}
	
	private WebTestClient.BodyContentSpec getAndVerifyInsuranceCompany(int insuranceCompanyId, HttpStatus expectedStatus) {
		return client.get()
			.uri("/insurance-company-composite/" + insuranceCompanyId)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody();
	}
}
