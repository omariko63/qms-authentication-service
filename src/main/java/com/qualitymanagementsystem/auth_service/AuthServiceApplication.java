package com.qualitymanagementsystem.auth_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.qualitymanagementsystem.auth_service",
		"common"
})
@EnableJpaRepositories(basePackages = {
		"com.qualitymanagementsystem.auth_service",
		"common.repository"
})
@EntityScan(basePackages = {
		"com.qualitymanagementsystem.auth_service",
		"common.model"
})
public class AuthServiceApplication {

	public static void main(String[] args) {

        SpringApplication.run(AuthServiceApplication.class, args);
        System.out.println("\n\nAuth Service is Compiled!!!!!\n");
	}

}
