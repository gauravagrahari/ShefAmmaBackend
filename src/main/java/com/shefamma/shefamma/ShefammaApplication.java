package com.shefamma.shefamma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = {"com.shefamma.shefamma", "com.shefamma.shefamma.config"})
public class ShefammaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShefammaApplication.class, args);
		System.out.println("Server port: " + System.getenv("PORT"));

	}

}

