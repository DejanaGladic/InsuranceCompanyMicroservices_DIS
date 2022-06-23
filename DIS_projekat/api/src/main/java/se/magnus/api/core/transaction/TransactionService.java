package se.magnus.api.core.transaction;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;


public interface TransactionService {

	Transaction createTransaction(@RequestBody Transaction body);
	
	@GetMapping(value = "/transaction", produces = "application/json")
	Flux<Transaction> getTransactions(@RequestParam(value = "insuranceCompanyId", required = true) int insuranceCompanyId);
	
	 void deleteTransactions(@RequestParam(value = "insuranceCompanyId", required = true)  int insuranceCompanyId);
}