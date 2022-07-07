package se.magnus.microservices.composite.insurancecompany.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.magnus.api.event.Event;
import se.magnus.api.core.employee.Employee;
import se.magnus.api.core.employee.EmployeeService;
import se.magnus.api.core.insuranceCompany.InsuranceCompany;
import se.magnus.api.core.insuranceCompany.InsuranceCompanyService;
import se.magnus.api.core.insuranceOffer.InsuranceOffer;
import se.magnus.api.core.insuranceOffer.InsuranceOfferService;
import se.magnus.api.core.transaction.Transaction;
import se.magnus.api.core.transaction.TransactionService;
import se.magnus.util.exceptions.InvalidInputException;
import se.magnus.util.exceptions.NotFoundException;
import se.magnus.util.http.HttpErrorInfo;

import java.io.IOException;

import static reactor.core.publisher.Flux.empty;
import static se.magnus.api.event.Event.Type.CREATE;
import static se.magnus.api.event.Event.Type.DELETE;

@EnableBinding(InsuranceCompanyCompositeIntegration.MessageSources.class)
@Component
public class InsuranceCompanyCompositeIntegration
		implements EmployeeService, InsuranceCompanyService, InsuranceOfferService, TransactionService {

	private static final Logger LOG = LoggerFactory.getLogger(InsuranceCompanyCompositeIntegration.class);

	private WebClient webClient;
	private final ObjectMapper mapper;
	private final WebClient.Builder webClientBuilder;

	private final String insuranceCompanyServiceUrl= "http://insuranceCompany";
	private final String employeeServiceUrl= "http://employee";
	private final String insuranceOfferServiceUrl= "http://insuranceOffer";
	private final String transactionServiceUrl= "http://transaction";

	private MessageSources messageSources;

	public interface MessageSources {

		String OUTPUT_INSURANCE_COMPANY = "output-insurance-companies";
		String OUTPUT_EMPLOYEES = "output-employees";
		String OUTPUT_INSURANCE_OFFER = "output-insurance-offers";
		String OUTPUT_TRANSACTIONS = "output-transactions";

		@Output(OUTPUT_INSURANCE_COMPANY)
		MessageChannel outputInsuranceCompanies();

		@Output(OUTPUT_EMPLOYEES)
		MessageChannel outputEmployees();

		@Output(OUTPUT_INSURANCE_OFFER)
		MessageChannel outputInsuranceOffers();

		@Output(OUTPUT_TRANSACTIONS)
		MessageChannel outputTransactions();
	}

	@Autowired
	public InsuranceCompanyCompositeIntegration(WebClient.Builder webClientBuilder, ObjectMapper mapper,
												MessageSources messageSources) {

		this.webClientBuilder = webClientBuilder;
		this.mapper = mapper;
		this.messageSources = messageSources;
	}

	@Override
	public InsuranceCompany createInsuranceCompany(InsuranceCompany body) {

		messageSources.outputInsuranceCompanies().send(MessageBuilder.withPayload(new Event(CREATE, body.getInsuranceCompanyId(), body)).build());
		return body;
	}

	public Mono<InsuranceCompany> getInsuranceCompany(int insuranceCompanyId) {
		String url = insuranceCompanyServiceUrl + "/insuranceCompany/" + insuranceCompanyId;
		LOG.debug("Will call the getInsuranceCompany API on URL: {}", url);

		return getWebClient().get().uri(url).retrieve().bodyToMono(InsuranceCompany.class).log().onErrorMap(WebClientResponseException.class, ex -> handleException(ex));
	}

	@Override
	public void deleteInsuranceCompany(int insuranceCompanyId) {
		messageSources.outputInsuranceCompanies().send(MessageBuilder.withPayload(new Event(DELETE, insuranceCompanyId, null)).build());
	}

	@Override
	public Employee createEmployee(Employee body) {

		messageSources.outputEmployees().send(MessageBuilder.withPayload(new Event(CREATE, body.getInsuranceCompanyId(), body)).build());
		return body;
	}

	public Flux<Employee> getEmployees(int insuranceCompanyId) {

		String url = employeeServiceUrl + "/employee?insuranceCompanyId=" + insuranceCompanyId;

		LOG.debug("Will call the getEmployees API on URL: {}", url);

		// Return an empty result if something goes wrong to make it possible for the composite service to return partial responses
		return getWebClient().get().uri(url).retrieve().bodyToFlux(Employee.class).log().onErrorResume(error -> empty());
	}

	@Override
	public void deleteEmployees(int insuranceCompanyId) {
		messageSources.outputEmployees().send(MessageBuilder.withPayload(new Event(DELETE, insuranceCompanyId, null)).build());
	}

	@Override
	public Transaction createTransaction(Transaction body) {
		messageSources.outputTransactions().send(MessageBuilder.withPayload(new Event(CREATE, body.getInsuranceCompanyId(), body)).build());
		return body;
	}

	public Flux<Transaction> getTransactions(int insuranceCompanyId) {

		String url = transactionServiceUrl + "/transaction?insuranceCompanyId=" + insuranceCompanyId;

		LOG.debug("Will call the getTransactions API on URL: {}", url);

		// Return an empty result if something goes wrong to make it possible for the composite service to return partial responses
		return getWebClient().get().uri(url).retrieve().bodyToFlux(Transaction.class).log().onErrorResume(error -> empty());
	}

	@Override
	public void deleteTransactions(int insuranceCompanyId) {
		messageSources.outputTransactions().send(MessageBuilder.withPayload(new Event(DELETE, insuranceCompanyId, null)).build());
	}
	@Override
	public InsuranceOffer createInsuranceOffer(InsuranceOffer body) {
		messageSources.outputInsuranceOffers().send(MessageBuilder.withPayload(new Event(CREATE, body.getInsuranceCompanyId(), body)).build());
		return body;
	}

	public Flux<InsuranceOffer> getInsuranceOffers(int insuranceCompanyId) {

		String url = insuranceOfferServiceUrl + "/insuranceOffer?insuranceCompanyId=" + insuranceCompanyId;

		LOG.debug("Will call the getInsuranceOffers API on URL: {}", url);

		// Return an empty result if something goes wrong to make it possible for the composite service to return partial responses
		return getWebClient().get().uri(url).retrieve().bodyToFlux(InsuranceOffer.class).log().onErrorResume(error -> empty());
	}

	@Override
	public void deleteInsuranceOffers(int insuranceCompanyId) {
		messageSources.outputInsuranceOffers().send(MessageBuilder.withPayload(new Event(DELETE, insuranceCompanyId, null)).build());
	}

	public Mono<Health> getInsuranceCompanyHealth() {
		return getHealth(insuranceCompanyServiceUrl);
	}

	public Mono<Health> getEmployeeHealth() {
		return getHealth(employeeServiceUrl);
	}

	public Mono<Health> getInsuranceOfferHealth() {
		return getHealth(insuranceOfferServiceUrl);
	}

	public Mono<Health> getTransactionHealth() {
		return getHealth(transactionServiceUrl);
	}

	private Mono<Health> getHealth(String url) {
		url += "/actuator/health";
		LOG.debug("Will call the Health API on URL: {}", url);
		return getWebClient().get().uri(url).retrieve().bodyToMono(String.class)
				.map(s -> new Health.Builder().up().build())
				.onErrorResume(ex -> Mono.just(new Health.Builder().down(ex).build()))
				.log();
	}

	private WebClient getWebClient() {
		if (webClient == null) {
			webClient = webClientBuilder.build();
		}
		return webClient;
	}

	private Throwable handleException(Throwable ex) {

		if (!(ex instanceof WebClientResponseException)) {
			LOG.warn("Got a unexpected error: {}, will rethrow it", ex.toString());
			return ex;
		}

		WebClientResponseException wcre = (WebClientResponseException)ex;

		switch (wcre.getStatusCode()) {

			case NOT_FOUND:
				return new NotFoundException(getErrorMessage(wcre));

			case UNPROCESSABLE_ENTITY :
				return new InvalidInputException(getErrorMessage(wcre));

			default:
				LOG.warn("Got a unexpected HTTP error: {}, will rethrow it", wcre.getStatusCode());
				LOG.warn("Error body: {}", wcre.getResponseBodyAsString());
				return ex;
		}
	}

    private String getErrorMessage(WebClientResponseException  ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ex.getMessage();
        }
    }

}
