package com.app.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.app.entities.administration.Agence;

@Service
public class PaiementService {

    private final RestTemplate restTemplate = new RestTemplate();

    public String initPaiement(Agence agence, double montant) {

        Map<String, Object> body = new HashMap<>();
        body.put("apikey", "YOUR_API_KEY");
        body.put("site_id", "YOUR_SITE_ID");
        body.put("transaction_id", UUID.randomUUID().toString());
        body.put("amount", montant);
        body.put("currency", "XOF");
        body.put("description", "Renouvellement abonnement");
        body.put("return_url", "https://ton-site/abonnement/success");
        body.put("notify_url", "https://ton-site/api/paiement/webhook");

        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://api-checkout.cinetpay.com/v2/payment",
                body,
                String.class
        );

        return response.getBody(); // URL de paiement
    }
}