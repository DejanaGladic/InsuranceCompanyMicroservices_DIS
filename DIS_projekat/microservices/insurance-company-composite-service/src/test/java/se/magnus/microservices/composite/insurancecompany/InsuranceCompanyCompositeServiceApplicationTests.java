package se.magnus.microservices.composite.insuranceCompany;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import se.magnus.api.core.insuranceCompany.InsuranceCompany;
import se.magnus.api.core.employee.Employee;
import se.magnus.api.core.insuranceOffer.InsuranceOffer;
import se.magnus.api.core.transaction.Transaction;
import se.magnus.microservices.composite.insurancecompany.services.InsuranceCompanyCompositeIntegration;

import se.magnus.util.exceptions.InvalidInputException;
import se.magnus.util.exceptions.NotFoundException;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
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
			thenReturn(new InsuranceCompany(INSURANCE_COMPANY_ID_OK, "name","city", "address", "phoneNumber", "mock-address"));
		
		when(compositeIntegration.getEmployees(INSURANCE_COMPANY_ID_OK)).
			thenReturn(singletonList(new Employee(INSURANCE_COMPANY_ID_OK, 1, "Dejana","Gladic", "Education 1", "Specialization 1", "mock-address")));
		
		when(compositeIntegration.getInsuranceOffers(INSURANCE_COMPANY_ID_OK)).
			thenReturn(singletonList(new InsuranceOffer(INSURANCE_COMPANY_ID_OK, 1, "OfferName 1","typeOfferProgram 1", "typeInsuranceCoverage 1",570.67, "currencyOffer 1","mock-address")));

		when(compositeIntegration.getTransactions(INSURANCE_COMPANY_ID_OK)).
			thenReturn(singletonList(new Transaction(INSURANCE_COMPANY_ID_OK, 1, "typeTransaction 1",new Date(), 570.67, "currencyOffer 1", "accountNumber 1","policeNumber 1", "mock-address")));
			
		when(compositeIntegration.getInsuranceCompany(INSURANCE_COMPANY_ID_NOT_FOUND)).thenThrow(new NotFoundException("NOT FOUND: " + INSURANCE_COMPANY_ID_NOT_FOUND));

		when(compositeIntegration.getInsuranceCompany(INSURANCE_COMPANY_ID_INVALID)).thenThrow(new InvalidInputException("INVALID: " + INSURANCE_COMPANY_ID_INVALID));
	}

	@Test
	public void contextLoads() {
	}

	@Test
	public void getInsuranceCompanyById() {

		client.get().uri("/insurance-company-composite/" + INSURANCE_COMPANY_ID_OK).accept(APPLICATION_JSON).exchange().expectStatus()
				.isOk().expectHeader().contentType(APPLICATION_JSON).expectBody().jsonPath("$.insuranceCompanyId")
				.isEqualTo(INSURANCE_COMPANY_ID_OK).jsonPath("$.employees.length()").isEqualTo(1)
				.jsonPath("$.insuranceOffers.length()").isEqualTo(1)
				.jsonPath("$.transactions.length()").isEqualTo(1);
	}

	@Test
	public void getInsuranceCompanyNotFound() {

		client.get().uri("/insurance-company-composite/" + INSURANCE_COMPANY_ID_NOT_FOUND).accept(APPLICATION_JSON).exchange()
				.expectStatus().isNotFound().expectHeader().contentType(APPLICATION_JSON).expectBody()
				.jsonPath("$.path").isEqualTo("/insurance-company-composite/" + INSURANCE_COMPANY_ID_NOT_FOUND).jsonPath("$.message")
				.isEqualTo("NOT FOUND: " + INSURANCE_COMPANY_ID_NOT_FOUND);
	}

	@Test
	public void getInsuranceCompanyInvalidInput() {

		client.get().uri("/insurance-company-composite/" + INSURANCE_COMPANY_ID_INVALID).accept(APPLICATION_JSON).exchange().expectStatus()
				.isEqualTo(UNPROCESSABLE_ENTITY).expectHeader().contentType(APPLICATION_JSON).expectBody()
				.jsonPath("$.path").isEqualTo("/insurance-company-composite/" + INSURANCE_COMPANY_ID_INVALID).jsonPath("$.message")
				.isEqualTo("INVALID: " + INSURANCE_COMPANY_ID_INVALID);
	}
}
