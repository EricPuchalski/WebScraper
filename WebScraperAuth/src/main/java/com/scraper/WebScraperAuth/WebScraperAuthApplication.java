package com.scraper.WebScraperAuth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class WebScraperAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebScraperAuthApplication.class, args);
	}

}
