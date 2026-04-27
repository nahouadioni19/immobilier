package com.app.test;

import org.springframework.boot.CommandLineRunner;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MailStartupTest implements CommandLineRunner {

    private final JavaMailSender mailSender;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("====================================");
        System.out.println("✅ JavaMailSender chargé : " + mailSender.getClass().getName());
        System.out.println("====================================");
    }
}
