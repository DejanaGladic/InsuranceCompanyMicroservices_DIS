package se.magnus.api.composite.insuranceCompany;

import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import reactor.core.publisher.Mono;

@Api(description = "REST API for composite insurance company information.")
public interface InsuranceCompanyCompositeService {

	@ApiOperation(value = "${api.insurance-company-composite.create-composite-insurance-company.description}", notes = "${api.insurance-company-composite.create-composite-insurance-company.notes}")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
			@ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fail. See response message for more information.") })
	@PostMapping(value = "/insurance-company-composite", consumes = "application/json")
	void createCompositeInsuranceCompany(@RequestBody InsuranceCompanyAggregate body);

	
	@ApiOperation(value = "${api.insurance-company-composite.get-composite-insurance-company.description}", notes = "${api.insurance-company-composite.get-composite-insurance-company.notes}")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
			@ApiResponse(code = 404, message = "Not found, the specified id does not exist."),
			@ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fails. See response message for more information.") })

	@GetMapping(value = "/insurance-company-composite/{insuranceCompanyId}", produces = "application/json")
	Mono<InsuranceCompanyAggregate> getCompositeInsuranceCompany(@PathVariable int insuranceCompanyId);

	
	@ApiOperation(value = "${api.insurance-company-composite.delete-composite-insurance-company.description}", notes = "${api.insurance-company-composite.delete-composite-insurance-company.notes}")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
			@ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fail. See response message for more information.") })
	@DeleteMapping(value = "/insurance-company-composite/{insuranceCompanyId}")
	void deleteCompositeInsuranceCompany(@PathVariable int insuranceCompanyId);

}