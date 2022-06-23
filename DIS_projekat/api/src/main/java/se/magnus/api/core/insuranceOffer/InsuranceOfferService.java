package se.magnus.api.core.insuranceOffer;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;


public interface InsuranceOfferService {

    InsuranceOffer createInsuranceOffer(@RequestBody InsuranceOffer body);
    
    @GetMapping(value = "/insuranceOffer", produces = "application/json")
	Flux<InsuranceOffer> getInsuranceOffers(@RequestParam(value = "insuranceCompanyId", required = true) int insuranceCompanyId);
    
    void deleteInsuranceOffers(@RequestParam(value = "insuranceCompanyId", required = true) int insuranceCompanyId);
    
}