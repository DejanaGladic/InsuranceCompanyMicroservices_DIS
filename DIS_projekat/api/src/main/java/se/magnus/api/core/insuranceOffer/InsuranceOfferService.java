package se.magnus.api.core.insuranceOffer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;

public interface InsuranceOfferService {
	@GetMapping(value = "/insuranceOffer", produces = "application/json")
	List<InsuranceOffer> getInsuranceOffers(
			@RequestParam(value = "insuranceCompanyId", required = true) int insuranceCompanyId);
}