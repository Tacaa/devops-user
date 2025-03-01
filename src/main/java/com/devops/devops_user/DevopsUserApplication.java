package com.devops.devops_user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class DevopsUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevopsUserApplication.class, args);
	}

}
