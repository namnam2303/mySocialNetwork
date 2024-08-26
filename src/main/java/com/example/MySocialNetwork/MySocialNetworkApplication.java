package com.example.MySocialNetwork;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MySocialNetworkApplication {

	public static void main(String[] args) {
		SpringApplication.run(MySocialNetworkApplication.class, args);
	}

}
