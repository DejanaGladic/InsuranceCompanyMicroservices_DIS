package se.magnus.microservices.core.insuranceoffer;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@ComponentScan("se.magnus")
public class InsuranceOfferServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InsuranceOfferServiceApplication.class, args);
	}

}
