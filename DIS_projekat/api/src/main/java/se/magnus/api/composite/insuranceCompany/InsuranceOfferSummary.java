package se.magnus.api.composite.insuranceCompany;

public class InsuranceOfferSummary {
	private int insuranceOfferId;
	private String offerName;
	private double price;
	private String currencyOffer;

	public InsuranceOfferSummary() {
		this.insuranceOfferId = 0;
		this.offerName = null;
		this.price = 0.0;
		this.currencyOffer = null;
	}

	public InsuranceOfferSummary(int insuranceOfferId, String offerName, double price, String currencyOffer) {
		this.insuranceOfferId = insuranceOfferId;
		this.offerName = offerName;
		this.price = price;
		this.currencyOffer = currencyOffer;
	}

	public int getInsuranceOfferId() {
		return insuranceOfferId;
	}

	public String getOfferName() {
		return offerName;
	}

	public double getPrice() {
		return price;
	}

	public String getCurrencyOffer() {
		return currencyOffer;
	}
}
