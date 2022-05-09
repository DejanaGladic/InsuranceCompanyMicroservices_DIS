package se.magnus.microservices.composite.insurancecompany.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
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
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpMethod.GET;

@Component
public class InsuranceCompanyCompositeIntegration
		implements EmployeeService, InsuranceCompanyService, InsuranceOfferService, TransactionService {

	private static final Logger LOG = LoggerFactory.getLogger(InsuranceCompanyCompositeIntegration.class);

	private final RestTemplate restTemplate;
	private final ObjectMapper mapper;

	private final String insuranceCompanyServiceUrl;
	private final String employeeServiceUrl;
	private final String insuranceOfferServiceUrl;
	private final String transactionServiceUrl;

	@Autowired
	public InsuranceCompanyCompositeIntegration(RestTemplate restTemplate, ObjectMapper mapper,

			@Value("${app.insurance-company-service.host}") String insuranceCompanyServiceHost,
			@Value("${app.insurance-company-service.port}") int insuranceCompanyServicePort,

			@Value("${app.employee-service.host}") String employeeServiceHost,
			@Value("${app.employee-service.port}") int employeeServicePort,

			@Value("${app.insurance-offer-service.host}") String insuranceOfferServiceHost,
			@Value("${app.insurance-offer-service.port}") int insuranceOfferServicePort,

			@Value("${app.transaction-service.host}") String transactionServiceHost,
			@Value("${app.transaction-service.port}") int transactionServicePort) {

		this.restTemplate = restTemplate;
		this.mapper = mapper;

		insuranceCompanyServiceUrl = "http://" + insuranceCompanyServiceHost + ":" + insuranceCompanyServicePort
				+ "/insuranceCompany/";
		employeeServiceUrl = "http://" + employeeServiceHost + ":" + employeeServicePort
				+ "/employee?insuranceCompanyId=";
		insuranceOfferServiceUrl = "http://" + insuranceOfferServiceHost + ":" + insuranceOfferServicePort
				+ "/insuranceOffer?insuranceCompanyId=";
		transactionServiceUrl = "http://" + transactionServiceHost + ":" + transactionServicePort
				+ "/transaction?insuranceCompanyId=";
	}

	public InsuranceCompany getInsuranceCompany(int insuranceCompanyId) {

		try {
			String url = insuranceCompanyServiceUrl + insuranceCompanyId;
			LOG.debug("Will call getInsuranceCompany API on URL: {}", url);

			InsuranceCompany insuranceCompany = restTemplate.getForObject(url, InsuranceCompany.class);
			LOG.debug("Found a insurance company with id: {}", insuranceCompany.getInsuranceCompanyId());

			return insuranceCompany;

		} catch (HttpClientErrorException ex) {

			switch (ex.getStatusCode()) {

			case NOT_FOUND:
				throw new NotFoundException(getErrorMessage(ex));

			case UNPROCESSABLE_ENTITY:
				throw new InvalidInputException(getErrorMessage(ex));

			default:
				LOG.warn("Got a unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
				LOG.warn("Error body: {}", ex.getResponseBodyAsString());
				throw ex;
			}
		}
	}

	private String getErrorMessage(HttpClientErrorException ex) {
		try {
			return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
		} catch (IOException ioex) {
			return ex.getMessage();
		}
	}

	public List<Employee> getEmployees(int insuranceCompanyId) {

		try {
			String url = employeeServiceUrl + insuranceCompanyId;

			LOG.debug("Will call getEmployees API on URL: {}", url);
			List<Employee> employees = restTemplate
					.exchange(url, GET, null, new ParameterizedTypeReference<List<Employee>>() {
					}).getBody();

			LOG.debug("Found {} employees for a insurance company with id: {}", employees.size(), insuranceCompanyId);
			return employees;

		} catch (Exception ex) {
			LOG.warn("Got an exception while requesting recommendations, return zero employees: {}",
					ex.getMessage());
			return new ArrayList<>();
		}
	}

	public List<InsuranceOffer> getInsuranceOffers(int insuranceCompanyId) {

		try {
			String url = insuranceOfferServiceUrl + insuranceCompanyId;

			LOG.debug("Will call getInsuranceOffers API on URL: {}", url);
			List<InsuranceOffer> insuranceOffers = restTemplate
					.exchange(url, GET, null, new ParameterizedTypeReference<List<InsuranceOffer>>() {
					}).getBody();

			LOG.debug("Found {} insurance offers for a insurance company with id: {}", insuranceOffers.size(), insuranceCompanyId);
			return insuranceOffers;

		} catch (Exception ex) {
			LOG.warn("Got an exception while requesting insurance offer, return zero insurance offers: {}", ex.getMessage());
			return new ArrayList<>();
		}
	}
	
	public List<Transaction> getTransactions(int insuranceCompanyId) {

		try {
			String url = transactionServiceUrl + insuranceCompanyId;

			LOG.debug("Will call getTransactions API on URL: {}", url);
			List<Transaction> transactions = restTemplate
					.exchange(url, GET, null, new ParameterizedTypeReference<List<Transaction>>() {
					}).getBody();

			LOG.debug("Found {} transactions for a insurance company with id: {}", transactions.size(), insuranceCompanyId);
			return transactions;

		} catch (Exception ex) {
			LOG.warn("Got an exception while requesting transactions, return zero transactions: {}", ex.getMessage());
			return new ArrayList<>();
		}
	}

}
