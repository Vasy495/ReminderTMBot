package com.tinkov.remindertmbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ReminderTmBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReminderTmBotApplication.class, args);
    }

}
