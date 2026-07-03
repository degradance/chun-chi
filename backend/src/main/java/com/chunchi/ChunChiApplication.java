package com.chunchi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ChunChiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChunChiApplication.class, args);
    }
}
