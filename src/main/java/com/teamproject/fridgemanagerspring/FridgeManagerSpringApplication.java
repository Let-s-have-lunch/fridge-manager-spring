package com.teamproject.fridgemanagerspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class FridgeManagerSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(FridgeManagerSpringApplication.class, args);
    }

}
