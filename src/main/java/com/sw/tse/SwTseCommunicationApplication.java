package com.sw.tse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableFeignClients
@SpringBootApplication
public class SwTseCommunicationApplication {

	public static void main(String[] args) {
		SpringApplication.run(SwTseCommunicationApplication.class, args);
	}
}
	