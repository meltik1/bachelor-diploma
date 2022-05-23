package com.example.diplomasagaorderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DiplomaSagaOrderServiceApplication {

    public static void main(String[] args) {
        System.setProperty("reactor.netty.ioWorkerCount", "12");
        SpringApplication.run(DiplomaSagaOrderServiceApplication.class, args);
    }

}
