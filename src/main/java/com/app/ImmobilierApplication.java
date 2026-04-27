package com.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;


@EnableCaching
@SpringBootApplication
public class ImmobilierApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        // Important pour WildFly : indique la classe source de Spring Boot
        return builder.sources(ImmobilierApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(ImmobilierApplication.class, args);
    }
}