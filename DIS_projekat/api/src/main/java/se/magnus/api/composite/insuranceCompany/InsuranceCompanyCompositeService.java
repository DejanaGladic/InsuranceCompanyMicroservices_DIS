package se.magnus.api.composite.insuranceCompany;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(description = "REST API for composite insurance company information.")
public interface InsuranceCompanyCompositeService {

    /**
@@ -11,6 +18,14 @@ public interface MovieCompositeService {
     * @param movieId
     * @return the composite movie info, if found, else null
     */
	@ApiOperation(
	        value = "${api.insurance-company-composite.get-composite-insurance-company.description}",
	        notes = "${api.insurance-company-composite.get-composite-insurance-company.notes}")
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
        @ApiResponse(code = 404, message = "Not found, the specified id does not exist."),
        @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fails. See response message for more information.")
    })

    @GetMapping(
        value    = "/insurance-company-composite/{insuranceCompanyId}",
        produces = "application/json")
    InsuranceCompanyAggregate getInsuranceCompany(@PathVariable int insuranceCompanyId);
}