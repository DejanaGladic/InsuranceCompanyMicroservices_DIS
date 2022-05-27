package se.magnus.api.core.employee;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;

public interface EmployeeService {
	@PostMapping(
	        value    = "/employee",
	        consumes = "application/json",
	        produces = "application/json")
	Employee createEmployee(@RequestBody Employee body);
	
	@GetMapping(value = "/employee", produces = "application/json")
	List<Employee> getEmployees(@RequestParam(value = "insuranceCompanyId", required = true) int insuranceCompanyId);
	
	@DeleteMapping(value = "/employee")
    void deleteEmployee(@RequestParam(value = "insuranceCompanyId", required = true)  int insuranceCompanyId);
}