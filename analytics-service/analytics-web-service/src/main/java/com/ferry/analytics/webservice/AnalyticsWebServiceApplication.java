package com.ferry.analytics.webservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

/************************
 * Made by [MR Ferry™]  *
 * on September 2026    *
 ************************/

@EnableScheduling
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class AnalyticsWebServiceApplication{

	static void main(String[] args){
		SpringApplication.run(AnalyticsWebServiceApplication.class, args);
	}

}
