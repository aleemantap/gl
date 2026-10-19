package com.project.gl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
public class ApplikasiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApplikasiApplication.class, args);
	}

}
