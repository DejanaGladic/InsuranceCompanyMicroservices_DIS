package se.magnus.api.core.transaction;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;

public interface TransactionService {
	
	@PostMapping(
	        value    = "/transaction",
	        consumes = "application/json",
	        produces = "application/json")
	Transaction createTransaction(@RequestBody Transaction body);
	
	@GetMapping(value = "/transaction", produces = "application/json")
	List<Transaction> getTransactions(
			@RequestParam(value = "insuranceCompanyId", required = true) int insuranceCompanyId);
	
	@DeleteMapping(value = "/transaction")
    void deleteTransactions(@RequestParam(value = "insuranceCompanyId", required = true)  int insuranceCompanyId);
}