package se.magnus.microservices.core.insuranceoffer.persistence;

import javax.persistence.*;

@Entity
@Table(name = "insuranceOffers", indexes = {
		@Index(name = "insuranceOffers_unique_idx", unique = true, columnList = "insuranceCompanyId,insuranceOfferId") })
public class InsuranceOfferEntity {

	@Id
	@GeneratedValue
	private int id;

	@Version
	private int version;

	private int insuranceCompanyId;
	private int insuranceOfferId;
	private String offerName;
	private String typeOfferProgram;
	private String typeInsuranceCoverage;
	private double price;
	private String currencyOffer;

	public InsuranceOfferEntity() {
	}

	public InsuranceOfferEntity(int insuranceCompanyId, int insuranceOfferId, String offerName, String typeOfferProgram,
			String typeInsuranceCoverage, double price, String currencyOffer) {
		this.insuranceCompanyId = insuranceCompanyId;
		this.insuranceOfferId = insuranceOfferId;
		this.offerName = offerName;
		this.typeOfferProgram = typeOfferProgram;
		this.typeInsuranceCoverage = typeInsuranceCoverage;
		this.price = price;
		this.currencyOffer = currencyOffer;
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

	public int getInsuranceOfferId() {
		return insuranceOfferId;
	}

	public String getOfferName() {
		return offerName;
	}

	public String getTypeOfferProgram() {
		return typeOfferProgram;
	}

	public String getTypeInsuranceCoverage() {
		return typeInsuranceCoverage;
	}

	public double getPrice() {
		return price;
	}

	public String getCurrencyOffer() {
		return currencyOffer;
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

	public void setInsuranceOfferId(int insuranceOfferId) {
		this.insuranceOfferId = insuranceOfferId;
	}

	public void setOfferName(String offerName) {
		this.offerName = offerName;
	}

	public void setTypeOfferProgram(String typeOfferProgram) {
		this.typeOfferProgram = typeOfferProgram;
	}

	public void setTypeInsuranceCoverage(String typeInsuranceCoverage) {
		this.typeInsuranceCoverage = typeInsuranceCoverage;
	}

	public void setPrice(double price) {
		this.price = price;
	}
	
	public void setCurrencyOffer(String currencyOffer) {
		this.currencyOffer = currencyOffer;
	}

}