package se.magnus.api.core.insuranceOffer;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;


public interface InsuranceOfferService {
	
	/*@PostMapping(
	        value    = "/insuranceOffer",
	        consumes = "application/json",
	        produces = "application/json")
	InsuranceOffer createInsuranceOffer(@RequestBody InsuranceOffer body);
	
	@GetMapping(value = "/insuranceOffer", produces = "application/json")
	List<InsuranceOffer> getInsuranceOffers(
			@RequestParam(value = "insuranceCompanyId", required = true) int insuranceCompanyId);
	
	@DeleteMapping(value = "/insuranceOffer")
    void deleteInsuranceOffers(@RequestParam(value = "insuranceCompanyId", required = true) int insuranceCompanyId);*/ 

    InsuranceOffer createInsuranceOffer(@RequestBody InsuranceOffer body);
    
    @GetMapping(value = "/insuranceOffer", produces = "application/json")
	Flux<InsuranceOffer> getInsuranceOffers(@RequestParam(value = "insuranceCompanyId", required = true) int insuranceCompanyId);
    
    void deleteInsuranceOffers(@RequestParam(value = "insuranceCompanyId", required = true) int insuranceCompanyId);
    
}