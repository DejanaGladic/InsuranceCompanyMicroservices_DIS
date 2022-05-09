package se.magnus.microservices.core.insurancecompany;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@ComponentScan("se.magnus")
public class InsuranceCompanyServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InsuranceCompanyServiceApplication.class, args);
	}

}
