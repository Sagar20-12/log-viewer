package com.example.logviewer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LogviewerApplication {
	public static void main(String[] args) {
        SpringApplication.run(LogviewerApplication.class, args);
	}

}
