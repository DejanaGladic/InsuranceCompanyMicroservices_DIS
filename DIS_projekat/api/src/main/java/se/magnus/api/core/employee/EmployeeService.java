package se.magnus.api.core.employee;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

public interface EmployeeService {
	
	Employee createEmployee(@RequestBody Employee body);
	
	@GetMapping(value = "/employee", produces = "application/json")
	Flux<Employee> getEmployees(@RequestParam(value = "insuranceCompanyId", required = true) int insuranceCompanyId);
	
	void deleteEmployees(@RequestParam(value = "insuranceCompanyId", required = true)  int insuranceCompanyId);
}