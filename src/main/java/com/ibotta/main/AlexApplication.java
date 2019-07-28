package com.ibotta.main;

import java.util.Collections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class AlexApplication {
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(AlexApplication.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", "3000"));
        app.run(args);
	}
}
