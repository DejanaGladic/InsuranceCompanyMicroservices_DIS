package se.magnus.microservices.composite.insurancecompany.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.magnus.api.core.employee.EmployeeService;
import se.magnus.api.core.employee.Employee;
import se.magnus.api.core.insuranceCompany.InsuranceCompany;
import se.magnus.api.core.insuranceCompany.InsuranceCompanyService;
import se.magnus.api.core.insuranceOffer.InsuranceOffer;
import se.magnus.api.core.insuranceOffer.InsuranceOfferService;
import se.magnus.api.core.transaction.Transaction;
import se.magnus.api.core.transaction.TransactionService;
import se.magnus.util.exceptions.*;
import se.magnus.util.http.HttpErrorInfo;

import java.io.IOException;
import static reactor.core.publisher.Flux.empty;

@Component
public class InsuranceCompanyCompositeIntegration
		implements EmployeeService, InsuranceCompanyService, InsuranceOfferService, TransactionService {

	private static final Logger LOG = LoggerFactory.getLogger(InsuranceCompanyCompositeIntegration.class);

	private final WebClient webClient;
	private final RestTemplate restTemplate;
	private final ObjectMapper mapper;

	private final String insuranceCompanyServiceUrl;
	private final String employeeServiceUrl;
	private final String insuranceOfferServiceUrl;
	private final String transactionServiceUrl;

	@Autowired
	public InsuranceCompanyCompositeIntegration(RestTemplate restTemplate, WebClient.Builder webClient, ObjectMapper mapper,

			@Value("${app.insurance-company-service.host}") String insuranceCompanyServiceHost,
			@Value("${app.insurance-company-service.port}") int insuranceCompanyServicePort,

			@Value("${app.employee-service.host}") String employeeServiceHost,
			@Value("${app.employee-service.port}") int employeeServicePort,

			@Value("${app.insurance-offer-service.host}") String insuranceOfferServiceHost,
			@Value("${app.insurance-offer-service.port}") int insuranceOfferServicePort,

			@Value("${app.transaction-service.host}") String transactionServiceHost,
			@Value("${app.transaction-service.port}") int transactionServicePort) {

		this.webClient = webClient.build();
		this.restTemplate = restTemplate;
		this.mapper = mapper;

		insuranceCompanyServiceUrl = "http://" + insuranceCompanyServiceHost + ":" + insuranceCompanyServicePort;
		employeeServiceUrl = "http://" + employeeServiceHost + ":" + employeeServicePort;
		insuranceOfferServiceUrl = "http://" + insuranceOfferServiceHost + ":" + insuranceOfferServicePort;
		transactionServiceUrl = "http://" + transactionServiceHost + ":" + transactionServicePort;
	}

	@Override
	public InsuranceCompany createInsuranceCompany(InsuranceCompany body) {

		try {
			String url = insuranceCompanyServiceUrl;
			LOG.debug("Will post a new insurance company to URL: {}", url);

			InsuranceCompany insuranceCompany = restTemplate.postForObject(url, body, InsuranceCompany.class);
			LOG.debug("Created a insurance company with id: {}", insuranceCompany.getInsuranceCompanyId());

			return insuranceCompany;

		} catch (HttpClientErrorException ex) {
			throw handleHttpClientException(ex);
		}
	}

	public Mono<InsuranceCompany> getInsuranceCompany(int insuranceCompanyId) {
		String url = insuranceCompanyServiceUrl + "/product/" + insuranceCompanyId;
		LOG.debug("Will call the getProduct API on URL: {}", url);

		return webClient.get().uri(url).retrieve().bodyToMono(InsuranceCompany.class).log().onErrorMap(WebClientResponseException.class, ex -> handleException(ex));
	}

	@Override
	public void deleteInsuranceCompany(int insuranceCompanyId) {
		try {
			String url = insuranceCompanyServiceUrl + "/" + insuranceCompanyId;
			LOG.debug("Will call the deleteInsuranceCompany API on URL: {}", url);

			restTemplate.delete(url);

		} catch (HttpClientErrorException ex) {
			throw handleHttpClientException(ex);
		}
	}

	@Override
	public Employee createEmployee(Employee body) {

		try {
			String url = employeeServiceUrl;
			LOG.debug("Will post a new employee to URL: {}", url);

			Employee employee = restTemplate.postForObject(url, body, Employee.class);
			LOG.debug("Created a employee with id: {}", employee.getEmployeeId());

			return employee;

		} catch (HttpClientErrorException ex) {
			throw handleHttpClientException(ex);
		}
	}

	public Flux<Employee> getEmployees(int insuranceCompanyId) {

		String url = employeeServiceUrl + "/employee?insuranceCompanyId=" + insuranceCompanyId;

		LOG.debug("Will call the getRecommendations API on URL: {}", url);

		// Return an empty result if something goes wrong to make it possible for the composite service to return partial responses
		return webClient.get().uri(url).retrieve().bodyToFlux(Employee.class).log().onErrorResume(error -> empty());
	}

	@Override
	public void deleteEmployees(int insuranceCompanyId) {
		try {
			String url = employeeServiceUrl + "?insuranceCompanyId=" + insuranceCompanyId;
			LOG.debug("Will call the deleteEmployees API on URL: {}", url);

			restTemplate.delete(url);

		} catch (HttpClientErrorException ex) {
			throw handleHttpClientException(ex);
		}
	}

	@Override
	public Transaction createTransaction(Transaction body) {

		try {
			String url = transactionServiceUrl;
			LOG.debug("Will post a new transaction to URL: {}", url);

			Transaction transaction = restTemplate.postForObject(url, body, Transaction.class);
			LOG.debug("Created a transaction with id: {}", transaction.getTransactionId());

			return transaction;

		} catch (HttpClientErrorException ex) {
			throw handleHttpClientException(ex);
		}
	}

	public Flux<Transaction> getTransactions(int insuranceCompanyId) {

		String url = transactionServiceUrl + "/employee?insuranceCompanyId=" + insuranceCompanyId;

		LOG.debug("Will call the getTransactions API on URL: {}", url);

		// Return an empty result if something goes wrong to make it possible for the composite service to return partial responses
		return webClient.get().uri(url).retrieve().bodyToFlux(Transaction.class).log().onErrorResume(error -> empty());
	}

	@Override
	public void deleteTransactions(int insuranceCompanyId) {
		try {
			String url = transactionServiceUrl + "?insuranceCompanyId=" + insuranceCompanyId;
			LOG.debug("Will call the deleteTransactions API on URL: {}", url);

			restTemplate.delete(url);

		} catch (HttpClientErrorException ex) {
			throw handleHttpClientException(ex);
		}
	}
	@Override
	public InsuranceOffer createInsuranceOffer(InsuranceOffer body) {

		try {
			String url = insuranceOfferServiceUrl;
			LOG.debug("Will post a new insurance offer to URL: {}", url);

			InsuranceOffer insuranceOffer = restTemplate.postForObject(url, body, InsuranceOffer.class);
			LOG.debug("Created a insurance offer with id: {}", insuranceOffer.getInsuranceOfferId());

			return insuranceOffer;

		} catch (HttpClientErrorException ex) {
			throw handleHttpClientException(ex);
		}
	}

	public Flux<InsuranceOffer> getInsuranceOffers(int insuranceCompanyId) {

		String url = insuranceOfferServiceUrl + "/insuranceOffer?insuranceCompanyId=" + insuranceCompanyId;

		LOG.debug("Will call the getInsuranceOffers API on URL: {}", url);

		// Return an empty result if something goes wrong to make it possible for the composite service to return partial responses
		return webClient.get().uri(url).retrieve().bodyToFlux(InsuranceOffer.class).log().onErrorResume(error -> empty());
	}

	@Override
	public void deleteInsuranceOffers(int insuranceCompanyId) {
		try {
			String url = insuranceOfferServiceUrl + "?insuranceCompanyId=" + insuranceCompanyId;
			LOG.debug("Will call the deleteInsuranceOffers API on URL: {}", url);

			restTemplate.delete(url);

		} catch (HttpClientErrorException ex) {
			throw handleHttpClientException(ex);
		}
	}
	
	private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
        switch (ex.getStatusCode()) {

        case NOT_FOUND:
            return new NotFoundException(getErrorMessage0(ex));

        case UNPROCESSABLE_ENTITY :
            return new InvalidInputException(getErrorMessage0(ex));

        default:
            LOG.warn("Got a unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
            LOG.warn("Error body: {}", ex.getResponseBodyAsString());
            return ex;
        }
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

	private String getErrorMessage0(HttpClientErrorException ex) {
		try {
			return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
		} catch (IOException ioex) {
			return ex.getMessage();
		}
	}

}
