package com.shefamma.shefamma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ShefammaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShefammaApplication.class, args);
	}
//	@Bean
//	public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> webServerFactoryCustomizer() {
//		return factory -> factory.setContextPath("/").setPort(8080).setAddress("0.0.0.0");
//	}
}

