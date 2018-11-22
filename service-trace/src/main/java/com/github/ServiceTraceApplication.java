package com.github;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@EnableZipkinServer
@SpringBootApplication
public class ServiceTraceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceTraceApplication.class, args);
    }
}
