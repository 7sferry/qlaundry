package com.ferry.notification.webservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

@SpringBootApplication
@EntityScan("com.ferry.notification.gateway")
@EnableJpaRepositories("com.ferry.notification.gateway")
public class NotificationWebServiceApplication{

	static void main(String[] args){
		SpringApplication.run(NotificationWebServiceApplication.class, args);
	}

}
