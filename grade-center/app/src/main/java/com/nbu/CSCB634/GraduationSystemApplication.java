package com.nbu.CSCB634;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.nbu.CSCB869"})
public class GraduationSystemApplication {
	public static void main(String[] args) {
		SpringApplication.run(GraduationSystemApplication.class, args);
	}

}
