package se.magnus.api.core.insuranceCompany;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

public interface InsuranceCompanyService {

	InsuranceCompany createInsuranceCompany(@RequestBody InsuranceCompany body);
	
	@GetMapping(value = "/insuranceCompany/{insuranceCompanyId}", produces = "application/json")
	Mono<InsuranceCompany> getInsuranceCompany(@PathVariable int insuranceCompanyId);
	
	void deleteInsuranceCompany(@PathVariable int insuranceCompanyId);
	
}
