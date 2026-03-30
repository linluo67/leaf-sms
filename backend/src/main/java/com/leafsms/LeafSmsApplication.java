package com.leafsms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LeafSmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeafSmsApplication.class, args);
    }

}
