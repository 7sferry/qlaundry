package com.ferry.user.webservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
@EntityScan("com.ferry.user.gateway")
@EnableJpaRepositories("com.ferry.user.gateway")
@EnableScheduling
public class UserWebServiceApplication{

	static void main(String[] args){
		SpringApplication.run(UserWebServiceApplication.class, args);
	}

}
