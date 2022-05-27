package se.magnus.microservices.core.transaction.persistence;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "transactions", indexes = {
		@Index(name = "transactions_unique_idx", unique = true, columnList = "insuranceCompanyId, transactionId") })
public class TransactionEntity {

	@Id
	@GeneratedValue
	private int id;

	@Version
	private int version;

	private int insuranceCompanyId;
	private int transactionId;
	private String typeTransaction;
	private Date dateTransaction;
	private double amount;
	private String currencyTransaction;
	private String accountNumber;
	private String policyNumber;

	public TransactionEntity() {
	}

	public TransactionEntity(int insuranceCompanyId, int transactionId, String typeTransaction, Date dateTransaction,
			double amount, String currencyTransaction, String accountNumber, String policyNumber) {
		this.insuranceCompanyId = insuranceCompanyId;
		this.transactionId = transactionId;
		this.typeTransaction = typeTransaction;
		this.dateTransaction = dateTransaction;
		this.amount = amount;
		this.currencyTransaction = currencyTransaction;
		this.accountNumber = accountNumber;
		this.policyNumber = policyNumber;
	}

	public int getId() {
		return id;
	}

	public int getVersion() {
		return version;
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

	public void setVersion(int version) {
		this.version = version;
	}

	public void setId(int id) {
		this.id = id;
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