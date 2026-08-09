package com.Project.URL_Shortner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableKafka
public class UrlShortnerApplication {

	public static void main(String[] args) {
		SpringApplication.run(UrlShortnerApplication.class, args);
	}

}
