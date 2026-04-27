package com.app.test;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class TestEmailService {

    private final JavaMailSender mailSender;

    public TestEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendTestEmail(String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Test Email Spring Boot");
        message.setText("✅ Votre configuration JavaMailSender fonctionne correctement !");
        message.setFrom("noreply@immobilier.com"); // adapte si nécessaire
        mailSender.send(message);
    }
}

