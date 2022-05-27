package se.magnus.api.core.insuranceCompany;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface InsuranceCompanyService {

	@PostMapping(
	        value    = "/insuranceCompany",
	        consumes = "application/json",
	        produces = "application/json")
	    Product createInsuranceCompany(@RequestBody InsuranceCompany body);
	
	@GetMapping(value = "/insuranceCompany/{insuranceCompanyId}", produces = "application/json")
	InsuranceCompany getInsuranceCompany(@PathVariable int insuranceCompanyId);
	
	@DeleteMapping(value = "/insuranceCompany/{insuranceCompanyId}}")
    void deleteInsuranceCompany(@PathVariable int insuranceCompanyId);
}
