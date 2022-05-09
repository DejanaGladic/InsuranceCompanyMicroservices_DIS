package se.magnus.api.composite.insuranceCompany;

public class InsuranceOfferSummary {
	private final int insuranceOfferId;
	private final String offerName;
	private final double price;
	private final String currencyOffer;

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
