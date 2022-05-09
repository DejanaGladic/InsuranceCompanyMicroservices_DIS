package se.magnus.microservices.composite.insurancecompany.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.api.composite.insuranceCompany.*;
import se.magnus.api.core.insuranceCompany.InsuranceCompany;
import se.magnus.api.core.insuranceOffer.InsuranceOffer;
import se.magnus.api.core.employee.Employee;
import se.magnus.api.core.transaction.Transaction;
import se.magnus.util.exceptions.NotFoundException;
import se.magnus.util.http.ServiceUtil;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class InsuranceCompanyCompositeServiceImpl implements InsuranceCompanyCompositeService {

	private final ServiceUtil serviceUtil;
	private InsuranceCompanyCompositeIntegration integration;

	@Autowired
	public InsuranceCompanyCompositeServiceImpl(ServiceUtil serviceUtil,
			InsuranceCompanyCompositeIntegration integration) {
		this.serviceUtil = serviceUtil;
		this.integration = integration;
	}

	@Override
	public InsuranceCompanyAggregate getInsuranceCompany(int insuranceCompanyId) {

		InsuranceCompany insuranceCompany = integration.getInsuranceCompany(insuranceCompanyId);
		if (insuranceCompany == null)
			throw new NotFoundException("No insurance company found for insuranceCompanyId: " + insuranceCompanyId);

		List<Employee> employees = integration.getEmployees(insuranceCompanyId);

		List<InsuranceOffer> insuranceOffers = integration.getInsuranceOffers(insuranceCompanyId);

		List<Transaction> transactions = integration.getTransactions(insuranceCompanyId);

		return createInsuranceCompanyAggregate(insuranceCompany, employees, insuranceOffers, transactions,
				serviceUtil.getServiceAddress());
	}

	private InsuranceCompanyAggregate createInsuranceCompanyAggregate(InsuranceCompany insuranceCompany,
			List<Employee> employees, List<InsuranceOffer> insuranceOffers, List<Transaction> transactions,
			String serviceAddress) {

		// 1. Setup product info
		int insuranceCompanyId = insuranceCompany.getInsuranceCompanyId();
		String name = insuranceCompany.getName();
		String city = insuranceCompany.getCity();
		String address = insuranceCompany.getAddress();
		String phoneNumber = insuranceCompany.getPhoneNumber();

		// 2. Copy summary recommendation info, if available
		List<EmployeeSummary> employeesSummaries = (employees == null) ? null
				: employees.stream().map(r -> new EmployeeSummary(r.getEmployeeId(), r.getName(), r.getSurname(),
						r.getSpecialization())).collect(Collectors.toList());

		// 3. Copy summary review info, if available
		List<InsuranceOfferSummary> insuranceOffersSummaries = (insuranceOffers == null) ? null
				: insuranceOffers.stream().map(r -> new InsuranceOfferSummary(r.getInsuranceOfferId(), r.getOfferName(),
						r.getPrice(), r.getCurrencyOffer())).collect(Collectors.toList());

		// 4. Copy summary transactions info, if available
		List<TransactionSummary> transactionsSummaries = (transactions == null) ? null
				: transactions.stream()
						.map(r -> new TransactionSummary(r.getTransactionId(), r.getTypeTransaction(), r.getAmount(),
								r.getCurrencyTransaction(), r.getPolicyNumber()))
						.collect(Collectors.toList());

		// 5. Create info regarding the involved microservices addresses
		String insuranceCompanyAddress = insuranceCompany.getServiceAddress();
		String employeeAddress = (employees != null && employees.size() > 0) ? employees.get(0).getServiceAddress()
				: "";
		String insuranceOfferAddress = (insuranceOffers != null && insuranceOffers.size() > 0)
				? insuranceOffers.get(0).getServiceAddress()
				: "";
		String transactionAddress = (transactions != null && transactions.size() > 0)
				? transactions.get(0).getServiceAddress()
				: "";
		ServiceAddresses serviceAddresses = new ServiceAddresses(serviceAddress, insuranceCompanyAddress,
				employeeAddress, insuranceOfferAddress, transactionAddress);

		return new InsuranceCompanyAggregate(insuranceCompanyId, name, city, address, phoneNumber, employeesSummaries,
				insuranceOffersSummaries, transactionsSummaries, serviceAddresses);
	}
}