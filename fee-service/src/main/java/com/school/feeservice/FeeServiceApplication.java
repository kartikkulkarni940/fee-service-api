package com.school.feeservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@OpenAPIDefinition(
		info = @Info(
				title = "Fee Service API",
				description = "Handles student fee payments, receipts, and integration with Student Service."
		)
)
@SpringBootApplication
@EnableFeignClients(basePackages = "com.school.feeservice.client")
public class FeeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FeeServiceApplication.class, args);
	}
}
