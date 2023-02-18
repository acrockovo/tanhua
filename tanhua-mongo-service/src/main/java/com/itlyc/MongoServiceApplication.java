package com.itlyc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
public class MongoServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MongoServiceApplication.class, args);
    }
}
