package com.app.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestEmailController {

    private final TestEmailService emailService;

    public TestEmailController(TestEmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/test-email")
    public String testEmail() {
        try {
            emailService.sendTestEmail("nahouadioni19@gmail.com"); // remplace par ton email
            return "Email envoyé ✅";
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de l'envoi de l'email : " + e.getMessage();
        }
    }
}
