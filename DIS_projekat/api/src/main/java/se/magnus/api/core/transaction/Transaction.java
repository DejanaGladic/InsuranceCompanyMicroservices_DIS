package se.magnus.api.core.transaction;

import java.util.Date;

public class Transaction {
	private int insuranceCompanyId;
	private int transactionId;
	private String typeTransaction;
	private Date dateTransaction;
	private double amount;
	private String currencyTransaction;
	private String accountNumber;
	private String policyNumber;
	private String serviceAddress;

	public Transaction() {
		insuranceCompanyId = 0;
		transactionId = 0;
		typeTransaction = null;
		dateTransaction = null;
		amount = 0.0;
		currencyTransaction = null;
		accountNumber = null;
		policyNumber = null;
		serviceAddress = null;
	}

	public Transaction(int insuranceCompanyId, int transactionId, String typeTransaction, Date dateTransaction,
			double amount, String currencyTransaction, String accountNumber, String policyNumber,
			String serviceAddress) {
		this.insuranceCompanyId = insuranceCompanyId;
		this.transactionId = transactionId;
		this.typeTransaction = typeTransaction;
		this.dateTransaction = dateTransaction;
		this.amount = amount;
		this.currencyTransaction = currencyTransaction;
		this.accountNumber = accountNumber;
		this.policyNumber = policyNumber;
		this.serviceAddress = serviceAddress;
	}

	public int getInsuranceCompanyId() {
		return insuranceCompanyId;
	}

	public int getTransactionId() {
		return transactionId;
	}

	public String getTypeTransaction() {
		return typeTransaction;
	}

	public Date getDateTransaction() {
		return dateTransaction;
	}

	public double getAmount() {
		return amount;
	}

	public String getCurrencyTransaction() {
		return currencyTransaction;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public String getPolicyNumber() {
		return policyNumber;
	}

	public String getServiceAddress() {
		return serviceAddress;
	}

	public void setServiceAddress(String serviceAddress) {
		this.serviceAddress = serviceAddress;
	}

	public void setInsuranceCompanyId(int insuranceCompanyId) {
		this.insuranceCompanyId = insuranceCompanyId;
	}

	public void setTransactionId(int transactionId) {
		this.transactionId = transactionId;
	}

	public void setTypeTransaction(String typeTransaction) {
		this.typeTransaction = typeTransaction;
	}

	public void setDateTransaction(Date dateTransaction) {
		this.dateTransaction = dateTransaction;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public void setCurrencyTransaction(String currencyTransaction) {
		this.currencyTransaction = currencyTransaction;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}
}
