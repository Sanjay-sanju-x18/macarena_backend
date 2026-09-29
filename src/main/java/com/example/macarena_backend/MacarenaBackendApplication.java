package com.example.macarena_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MacarenaBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MacarenaBackendApplication.class, args);
	}

}
