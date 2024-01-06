package com.shefamma.shefamma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = {"com.shefamma.shefamma", "com.shefamma.shefamma.config"})
public class ShefammaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShefammaApplication.class, args);
	}
//	@Bean
//	public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> webServerFactoryCustomizer() {
//		return factory -> factory.setPort(Integer.parseInt(System.getenv("PORT")));
//	}
}

