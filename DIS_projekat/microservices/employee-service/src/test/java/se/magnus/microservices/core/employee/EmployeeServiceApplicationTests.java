package se.magnus.microservices.core.employee;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.GenericMessage;
import se.magnus.api.event.Event;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.magnus.api.core.employee.*;
import se.magnus.microservices.core.employee.persistence.*;
import se.magnus.util.exceptions.InvalidInputException;
import se.magnus.api.core.insuranceCompany.*;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;
import static se.magnus.api.event.Event.Type.CREATE;
import static se.magnus.api.event.Event.Type.DELETE;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = { "eureka.client.enabled=false","spring.data.mongodb.port: 0", "spring.cloud.config.enabled=false" })
public class EmployeeServiceApplicationTests {

	@Autowired
	private WebTestClient client;

	@Autowired
	private EmployeeRepository repository;

	@Autowired
	private Sink channels;

	private AbstractMessageChannel input = null;
	
	@Before
	public void setupDb() {
		input = (AbstractMessageChannel) channels.input();
		repository.deleteAll().block();
	}

	@Test
	public void getEmployeesByInsuranceCompanyId() {

		int insuranceCompanyId = 1;

		sendCreateEmployeeEvent(insuranceCompanyId, 1);
		sendCreateEmployeeEvent(insuranceCompanyId, 2);
		sendCreateEmployeeEvent(insuranceCompanyId, 3);

		assertEquals(3, (long)repository.findByInsuranceCompanyId(insuranceCompanyId).count().block());
		assertEquals(1, (long)repository.findByName("Name 3").count().block());

		getAndVerifyEmployeesByInsuranceCompanyId(insuranceCompanyId, OK)
			.jsonPath("$.length()").isEqualTo(3)
			.jsonPath("$[2].insuranceCompanyId").isEqualTo(insuranceCompanyId)
			.jsonPath("$[2].employeeId").isEqualTo(3);
	}

	@Test
	public void deleteEmployees() {

		int insuranceCompanyId = 1;
		int employeeId = 1;

		sendCreateEmployeeEvent(insuranceCompanyId, employeeId);

		assertEquals(1, (long)repository.findByInsuranceCompanyId(insuranceCompanyId).count().block());

		sendDeleteEmployeeEvent(insuranceCompanyId);
		assertEquals(0, (long)repository.findByInsuranceCompanyId(insuranceCompanyId).count().block());

		sendDeleteEmployeeEvent(insuranceCompanyId);
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
	
	private WebTestClient.BodyContentSpec getAndVerifyEmployeesByInsuranceCompanyId(int insuranceCompanyId, HttpStatus expectedStatus) {
		return getAndVerifyEmployeesByInsuranceCompanyId("?insuranceCompanyId=" + insuranceCompanyId, expectedStatus);
	}

	private WebTestClient.BodyContentSpec getAndVerifyEmployeesByInsuranceCompanyId(String insuranceCompanyIdQuery,
			HttpStatus expectedStatus) {
		return client.get().uri("/employee" + insuranceCompanyIdQuery).accept(APPLICATION_JSON).exchange()
				.expectStatus().isEqualTo(expectedStatus).expectHeader().contentType(APPLICATION_JSON).expectBody();
	}

	//proveriti ove metode, nije mi bas logicno ovo
	private void sendCreateEmployeeEvent(int insuranceCompanyId, int employeeId) {
		Employee employee = new Employee(insuranceCompanyId, employeeId, "Name " + employeeId, "Surname " + employeeId, "Education " + employeeId,"Specialization " + employeeId, "SA");
		Event<Integer, InsuranceCompany> event = new Event(CREATE, null, employee);
		input.send(new GenericMessage<>(event));
	}

	private void sendDeleteEmployeeEvent(int insuranceCompanyId) {
		Event<Integer, InsuranceCompany> event = new Event(DELETE, insuranceCompanyId, null);
		input.send(new GenericMessage<>(event));
	}
}
