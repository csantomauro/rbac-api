package com.cs.rbac_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RbacApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(RbacApiApplication.class, args);
	}

}
