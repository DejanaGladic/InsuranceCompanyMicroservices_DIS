package se.magnus.microservices.core.employee;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.magnus.api.core.employee.*;
import se.magnus.microservices.core.employee.persistence.*;

import static org.junit.Assert.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = { "spring.data.mongodb.port: 0" })
public class EmployeeServiceApplicationTests {

	@Autowired
	private WebTestClient client;

	@Autowired
	private EmployeeRepository repository;

	@Before
	public void setupDb() {
		repository.deleteAll();
	}

	@Test
	public void getEmployeesByInsuranceCompanyId() {

		int insuranceCompanyId = 1;

		postAndVerifyEmployee(insuranceCompanyId, 1, OK);
		postAndVerifyEmployee(insuranceCompanyId, 2, OK);
		postAndVerifyEmployee(insuranceCompanyId, 3, OK);

		assertEquals(3, repository.findByInsuranceCompanyId(insuranceCompanyId).size());
	}

	@Test
	public void deleteEmployees() {

		int insuranceCompanyId = 1;
		int employeeId = 1;

		postAndVerifyEmployee(insuranceCompanyId, employeeId, OK);
		assertEquals(1, repository.findByInsuranceCompanyId(insuranceCompanyId).size());

		deleteAndVerifyEmployeesByInsuranceCompanyId(insuranceCompanyId, OK);
		assertEquals(0, repository.findByInsuranceCompanyId(insuranceCompanyId).size());

		deleteAndVerifyEmployeesByInsuranceCompanyId(insuranceCompanyId, OK);
	}

	@Test
	public void getEmployeesMissingParameter() {

		getAndVerifyEmployeesByInsuranceCompanyId("", BAD_REQUEST).jsonPath("$.path").isEqualTo("/employee")
				.jsonPath("$.message").isEqualTo("Required int parameter 'insuranceCompanyId' is not present");
	}

	@Test
	public void getEmployeesInvalidParameter() {

		getAndVerifyEmployeesByInsuranceCompanyId("?insuranceCompanyId=no-integer", BAD_REQUEST).jsonPath("$.path")
				.isEqualTo("/employee").jsonPath("$.message").isEqualTo("Type mismatch.");

	}

	@Test
	public void getEmployeesNotFound() {

		getAndVerifyEmployeesByInsuranceCompanyId("?insuranceCompanyId=113", OK).jsonPath("$.length()").isEqualTo(0);

	}

	@Test
	public void getEmployeesInvalidParameterNegativeValue() {

		int insuranceCompanyIdInvalid = -1;

		getAndVerifyEmployeesByInsuranceCompanyId("?insuranceCompanyId=" + insuranceCompanyIdInvalid,
				UNPROCESSABLE_ENTITY).jsonPath("$.path").isEqualTo("/employee").jsonPath("$.message")
						.isEqualTo("Invalid insuranceCompanyId: " + insuranceCompanyIdInvalid);

	}

	@Test
	public void postEmployee() {
		int insuranceCompanyId = 1;
		int employeeId = 1;
		postAndVerifyEmployee(insuranceCompanyId, employeeId, OK);
		assertNotNull(repository.findByEmployeeId(employeeId));
	}

	private WebTestClient.BodyContentSpec getAndVerifyEmployeesByInsuranceCompanyId(String insuranceCompanyIdQuery,
			HttpStatus expectedStatus) {
		return client.get().uri("/employee" + insuranceCompanyIdQuery).accept(APPLICATION_JSON).exchange()
				.expectStatus().isEqualTo(expectedStatus).expectHeader().contentType(APPLICATION_JSON).expectBody();
	}

	private WebTestClient.BodyContentSpec postAndVerifyEmployee(int insuranceCompanyId, int employeeId,
			HttpStatus expectedStatus) {
		Employee employee = new Employee(insuranceCompanyId, employeeId, "name 1", "surname 1", "education 1",
				"specialization 1", "SA");
		return client.post().uri("/employee").body(just(employee), Employee.class).accept(APPLICATION_JSON).exchange()
				.expectStatus().isEqualTo(expectedStatus).expectHeader().contentType(APPLICATION_JSON).expectBody();
	}

	private WebTestClient.BodyContentSpec deleteAndVerifyEmployeesByInsuranceCompanyId(int insuranceCompanyId,
			HttpStatus expectedStatus) {
		return client.delete().uri("/employee?insuranceCompanyId=" + insuranceCompanyId).accept(APPLICATION_JSON)
				.exchange().expectStatus().isEqualTo(expectedStatus).expectBody();
	}
}
