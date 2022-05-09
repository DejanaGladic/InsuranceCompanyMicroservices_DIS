package se.magnus.api.core.transaction;
import java.util.Date;

public class Transaction {
	private final int insuranceCompanyId;
	private final int transactionId;
	private final String typeTransaction;
	private final Date dateTransaction;
	private final double amount;
	private final String currencyTransaction;
	private final String accountNumber;
	private final String policyNumber;
	private final String serviceAddress;

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
}
