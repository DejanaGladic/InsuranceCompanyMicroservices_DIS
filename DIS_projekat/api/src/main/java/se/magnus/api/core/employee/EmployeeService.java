package se.magnus.api.core.employee;

import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface EmployeeService {
	@PostMapping(
	        value    = "/employee",
	        consumes = "application/json",
	        produces = "application/json")
	Employee createEmployee(@RequestBody Employee body);
	
	@GetMapping(value = "/employee", produces = "application/json")
	List<Employee> getEmployees(@RequestParam(value = "insuranceCompanyId", required = true) int insuranceCompanyId);
	
	@DeleteMapping(value = "/employee")
    void deleteEmployees(@RequestParam(value = "insuranceCompanyId", required = true)  int insuranceCompanyId);
}