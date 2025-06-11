package com.grupo5.ms_books_catalogue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsBooksCatalogueApplication {

	public static void main(String[] args) {

		SpringApplication.run(MsBooksCatalogueApplication.class, args);
		System.out.println("Hello World!");
	}

}
