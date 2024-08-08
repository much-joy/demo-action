package com.action;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class DemoActionApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(DemoActionApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("started successfully");
    }
}
