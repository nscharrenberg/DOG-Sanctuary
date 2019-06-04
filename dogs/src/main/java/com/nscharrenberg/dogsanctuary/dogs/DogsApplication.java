package com.nscharrenberg.dogsanctuary.dogs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class DogsApplication {
	public static void main(String[] args) {
		SpringApplication.run(DogsApplication.class, args);
	}
}
