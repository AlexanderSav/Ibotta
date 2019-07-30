package com.ibotta.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class DictionaryApplication {
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(DictionaryApplication.class);;
        app.run(args);
	}
}
