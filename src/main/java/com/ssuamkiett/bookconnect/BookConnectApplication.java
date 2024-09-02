package com.ssuamkiett.bookconnect;

import com.ssuamkiett.bookconnect.role.Role;
import com.ssuamkiett.bookconnect.role.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class 		BookConnectApplication {
	private static final Logger logger = LoggerFactory.getLogger(BookConnectApplication.class);
	public static void main(String[] args) {
		logger.info("BookConnect Application Started...");
		SpringApplication.run(BookConnectApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(RoleRepository roleRepository) {
		return args -> {
			if(roleRepository.findByName("USER").isEmpty()) {
				roleRepository.save(Role.builder().name("USER").build());
			}
		};
	}
}
