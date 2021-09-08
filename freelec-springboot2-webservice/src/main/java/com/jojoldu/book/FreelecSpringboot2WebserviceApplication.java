package com.jojoldu.book;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class FreelecSpringboot2WebserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FreelecSpringboot2WebserviceApplication.class, args);
    }

}
