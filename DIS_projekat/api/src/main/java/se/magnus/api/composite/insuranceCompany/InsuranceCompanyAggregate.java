package se.magnus.api.composite.insuranceCompany;

import java.util.List;

public class InsuranceCompanyAggregate {
	private int insuranceCompanyId;
	private String name;
	private String city;
	private String address;
	private String phoneNumber;
	private List<EmployeeSummary> employees;
	private List<InsuranceOfferSummary> insuranceOffers;
	private List<TransactionSummary> transactions;
	private ServiceAddresses serviceAddresses;

	public InsuranceCompanyAggregate() {
		this.insuranceCompanyId = 0;
		this.name = null;
		this.city = null;
		this.address = null;
		this.phoneNumber = null;
		this.employees = null;
		this.insuranceOffers = null;
		this.transactions = null;
		this.serviceAddresses = null;
	}
	
	public InsuranceCompanyAggregate(int insuranceCompanyId, String name, String city, String address,
			String phoneNumber, List<EmployeeSummary> employees, List<InsuranceOfferSummary> insuranceOffers,
			List<TransactionSummary> transactions, ServiceAddresses serviceAddresses) {
		this.insuranceCompanyId = insuranceCompanyId;
		this.name = name;
		this.city = city;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.employees = employees;
		this.insuranceOffers = insuranceOffers;
		this.transactions = transactions;
		this.serviceAddresses = serviceAddresses;
	}

	public int getInsuranceCompanyId() {
		return this.insuranceCompanyId;
	}

	public String getName() {
		return this.name;
	}

	public String getCity() {
		return this.city;
	}

	public String getAddress() {
		return this.address;
	}

	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	public List<EmployeeSummary> getEmployees() {
		return employees;
	}

	public List<InsuranceOfferSummary> getInsuranceOffers() {
		return insuranceOffers;
	}

	public List<TransactionSummary> getTransactions() {
		return transactions;
	}

	public ServiceAddresses getServiceAddresses() {
		return serviceAddresses;
	}
}