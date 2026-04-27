package com.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void envoyerMailDeConfirmation(String to, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Confirmation de votre inscription");
        message.setText("Bienvenue !\nVotre nom d'utilisateur est : " + username + "\nVeuillez le conserver.");
        mailSender.send(message);
    }
    
    public void sendConfirmationEmail(String toEmail, String codeDossier) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Confirmation de votre enregistrement");
        message.setText("Bonjour,\n\nVotre demande a été enregistrée avec succès. "
            + "Voici votre code dossier : " + codeDossier + "\n\nMerci.");
        mailSender.send(message);
    }
}
