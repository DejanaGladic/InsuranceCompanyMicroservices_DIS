package se.magnus.api.composite.insuranceCompany;

import java.util.Date;

public class TransactionSummary {
	private final int transactionId;
	private final String typeTransaction;
	private final double amount;
	private final String currencyTransaction;
	private final String policyNumber;

	public TransactionSummary() {
		this.transactionId = 0;
		this.typeTransaction = null;
		this.amount = 0.0;
		this.currencyTransaction = null;
		this.policyNumber = null;
	}

	public TransactionSummary(int transactionId, String typeTransaction, double amount, String currencyTransaction,
			String policyNumber) {
		this.transactionId = transactionId;
		this.typeTransaction = typeTransaction;
		this.amount = amount;
		this.currencyTransaction = currencyTransaction;
		this.policyNumber = policyNumber;
	}

	public int getTransactionId() {
		return transactionId;
	}

	public String getTypeTransaction() {
		return typeTransaction;
	}

	public double getAmount() {
		return amount;
	}

	public String getCurrencyTransaction() {
		return currencyTransaction;
	}

	public String getPolicyNumber() {
		return policyNumber;
	}
}
