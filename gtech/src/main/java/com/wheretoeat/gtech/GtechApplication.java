package com.wheretoeat.gtech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.convert.Jsr310Converters;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@ComponentScan(basePackages = {"com.wheretoeat.gtech.service", "com.wheretoeat.gtech.controller"})
@EntityScan(basePackageClasses = Jsr310Converters.class, basePackages = {"com.wheretoeat.gtech.entities"})
@EnableJpaRepositories(basePackages = {"com.wheretoeat.gtech.repo"})
public class GtechApplication {

	public static void main(String[] args) {
		SpringApplication.run(GtechApplication.class, args);
	}

}
