package se.magnus.api.core.insuranceOffer;

public class InsuranceOffer {
	private int insuranceCompanyId;
	private int insuranceOfferId;
	private String offerName;
	private String typeOfferProgram;
	private String typeInsuranceCoverage;
	private double price;
	private String currencyOffer;
	private String serviceAddress;

	public InsuranceOffer() {
		insuranceCompanyId = 0;
		insuranceOfferId = 0;
		offerName = null;
		typeOfferProgram = null;
		typeInsuranceCoverage = null;
		price = 0.0;
		currencyOffer = null;
		serviceAddress = null;
	}

	public InsuranceOffer(int insuranceCompanyId, int insuranceOfferId, String offerName, String typeOfferProgram,
			String typeInsuranceCoverage, double price, String currencyOffer, String serviceAddress) {
		this.insuranceCompanyId = insuranceCompanyId;
		this.insuranceOfferId = insuranceOfferId;
		this.offerName = offerName;
		this.typeOfferProgram = typeOfferProgram;
		this.typeInsuranceCoverage = typeInsuranceCoverage;
		this.price = price;
		this.currencyOffer = currencyOffer;
		this.serviceAddress = serviceAddress;
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

	public String getServiceAddress() {
		return serviceAddress;
	}
	
	public void setServiceAddress(String serviceAddress) {
		this.serviceAddress = serviceAddress;
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
