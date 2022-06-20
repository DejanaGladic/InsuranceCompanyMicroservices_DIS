package se.magnus.microservices.composite.insurancecompany.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
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
	private static final Logger LOG = LoggerFactory.getLogger(InsuranceCompanyCompositeServiceImpl.class);

	@Autowired
	public InsuranceCompanyCompositeServiceImpl(ServiceUtil serviceUtil,
			InsuranceCompanyCompositeIntegration integration) {
		this.serviceUtil = serviceUtil;
		this.integration = integration;
	}

	@Override
    public void createCompositeInsuranceCompany(InsuranceCompanyAggregate body) {

        try {

            LOG.debug("createCompositeInsuranceCompany: creates a new composite entity for insuranceCompanyId: {}", body.getInsuranceCompanyId());

            InsuranceCompany insuranceCompany = new InsuranceCompany(body.getInsuranceCompanyId(), body.getName(), body.getCity(),
            		body.getAddress(),body.getPhoneNumber(),  null);
            integration.createInsuranceCompany(insuranceCompany);

            if (body.getEmployees() != null) {
                body.getEmployees().forEach(r -> {
                	Employee employee = new Employee(body.getInsuranceCompanyId(), r.getEmployeeId(), r.getName(), r.getSurname(),null, r.getSpecialization(), null);
                    integration.createEmployee(employee);
                });
            }

            if (body.getInsuranceOffers() != null) {
                body.getInsuranceOffers().forEach(r -> {
                	InsuranceOffer insuranceOffer = new InsuranceOffer(body.getInsuranceCompanyId(), r.getInsuranceOfferId(), r.getOfferName(), null, null, r.getPrice(), r.getCurrencyOffer(), null);
                    integration.createInsuranceOffer(insuranceOffer);
                });
            }
            
            if (body.getTransactions() != null) {
                body.getTransactions().forEach(r -> {
                	Transaction transaction = new Transaction(body.getInsuranceCompanyId(), r.getTransactionId(), r.getTypeTransaction(),null,  r.getAmount(), r.getCurrencyTransaction(), null, r.getPolicyNumber(), null);
                    integration.createTransaction(transaction);
                });
            }

            LOG.debug("createCompositeInsuranceCompany: composite entites created for insuranceCompanyId: {}", body.getInsuranceCompanyId());

        } catch (RuntimeException re) {
            LOG.warn("createCompositeInsuranceCompany failed", re);
            throw re;
        }
    }

	
	@Override
	public Mono<InsuranceCompanyAggregate> getCompositeInsuranceCompany(int insuranceCompanyId) {

		return Mono.zip(
						values -> createInsuranceCompanyAggregate((InsuranceCompany) values[0], (List<Employee>) values[1], (List<InsuranceOffer>) values[2],(List<Transaction>) values[3], serviceUtil.getServiceAddress()),
						integration.getInsuranceCompany(insuranceCompanyId),
						integration.getEmployees(insuranceCompanyId).collectList(),
						integration.getInsuranceOffers(insuranceCompanyId).collectList(),
						integration.getTransactions(insuranceCompanyId).collectList())
				.doOnError(ex -> LOG.warn("getCompositeInsuranceCompany failed: {}", ex.toString()))
				.log();
	}

	@Override
    public void deleteCompositeInsuranceCompany(int insuranceCompanyId) {

        LOG.debug("deleteCompositeInsuranceCompany: Deletes a insurance company aggregate for insuranceCompanyId: {}", insuranceCompanyId);

        integration.deleteInsuranceCompany(insuranceCompanyId);

        integration.deleteEmployees(insuranceCompanyId);

        integration.deleteInsuranceOffers(insuranceCompanyId);
        
        integration.deleteTransactions(insuranceCompanyId);

        LOG.debug("getCompositeInsuranceCompanyt: aggregate entities deleted for insuranceCompanyId: {}", insuranceCompanyId);
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