package com.app.test;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MailSendTest {

    private final JavaMailSender mailSender;

    @PostConstruct
    public void testSend() {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("nahouadioni19@gmail.com"); // ou une autre adresse
            message.setSubject("TEST IMMOBILIER LOCAL");
            message.setText("Test d'envoi depuis Spring Boot (local)");

            mailSender.send(message);

            System.out.println("✅ MAIL DE TEST ENVOYÉ AVEC SUCCÈS");
        } catch (Exception e) {
            System.err.println("❌ ERREUR ENVOI MAIL : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
