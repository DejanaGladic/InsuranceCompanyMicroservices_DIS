package se.magnus.api.composite.insuranceCompany;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface InsuranceCompanyCompositeService {

    @GetMapping(
        value    = "/insurance-company-composite/{insuranceCompanyId}",
        produces = "application/json")
    InsuranceCompanyAggregate getInsuranceCompany(@PathVariable int insuranceCompanyId);
}