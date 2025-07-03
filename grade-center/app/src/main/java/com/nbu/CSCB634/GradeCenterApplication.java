package com.nbu.CSCB634;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.nbu.CSCB634"})
public class GradeCenterApplication {
	public static void main(String[] args) {
		SpringApplication.run(GradeCenterApplication.class, args);
	}

}
