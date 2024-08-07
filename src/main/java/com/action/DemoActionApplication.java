package com.action;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoActionApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(DemoActionApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
