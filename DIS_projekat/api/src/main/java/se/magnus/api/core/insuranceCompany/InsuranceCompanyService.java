package se.magnus.api.core.insuranceCompany;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface InsuranceCompanyService {

	@GetMapping(value = "/insuranceCompany/{insuranceCompanyId}", produces = "application/json")
	InsuranceCompany getInsuranceCompany(@PathVariable int insuranceCompanyId);
}
