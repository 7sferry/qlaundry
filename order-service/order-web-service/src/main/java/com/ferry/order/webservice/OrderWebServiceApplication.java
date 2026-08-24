package com.ferry.order.webservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/************************
 * Made by [MR Ferry™]  *
 * on Agustus 2026      *
 ************************/

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
@EntityScan("com.ferry.order.gateway")
@EnableJpaRepositories("com.ferry.order.gateway")
public class OrderWebServiceApplication{

	static void main(String[] args){
		SpringApplication.run(OrderWebServiceApplication.class, args);
	}

}
