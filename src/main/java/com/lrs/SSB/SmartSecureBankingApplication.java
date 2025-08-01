package com.lrs.SSB;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmartSecureBankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartSecureBankingApplication.class, args);
	}

}
