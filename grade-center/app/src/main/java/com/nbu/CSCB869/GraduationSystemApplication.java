package com.nbu.CSCB869;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.nbu.CSCB869"})
public class GraduationSystemApplication {
	public static void main(String[] args) {
		SpringApplication.run(GraduationSystemApplication.class, args);
	}

}
