package com.app.controller.abonne;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.app.dto.PaiementDto;
import com.app.entities.administration.Agence;
import com.app.security.UserPrincipal;
import com.app.service.PaiementService;
import com.app.service.administration.AgenceService;

@Controller
@RequestMapping("/paiement")
public class PaiementController {
	
	private final PaiementService service;
	private final AgenceService agenceService;
	
	public PaiementController(PaiementService service, AgenceService agenceService) {
		this.service = service;
		this.agenceService = agenceService;
	}
	
	@GetMapping("/expired")
	public String expiredPage() {
	    return "paiement/expired";
	}
	
	@GetMapping("/renew")
	public String payerAbonnement() {
	    return "paiement/renew";
	}
	
	/*@GetMapping("/revew")
	public String payerAbonnement(@AuthenticationPrincipal UserPrincipal principal) {

	    Agence agence = principal.getUtilisateur().getAgence();

	    // 🔐 sécurité SaaS
	    if (agence == null) {
	        throw new RuntimeException("Agence introuvable");
	    }

	    if (Boolean.TRUE.equals(agence.getBloque())) {
	        throw new RuntimeException("Agence bloquée");
	    }

	    // 🚫 éviter double paiement inutile
	    if (agence.getDateFinAbonnement() != null &&
	        LocalDate.now().isBefore(agence.getDateFinAbonnement())) {

	        throw new RuntimeException("Abonnement déjà actif");
	    }

	    String urlPaiement = service.initPaiement(
	            agence,
	            agence.getMontantAbonnement()
	    );

	    return "redirect:" + urlPaiement;
	}*/ //EN PRODUCTION
	
	
	@PostMapping("/api/paiement/webhook")
	public ResponseEntity<String> webhook(@RequestBody PaiementDto dto) {

	    if ("ACCEPTED".equals(dto.getStatus())) {

	        Agence agence = agenceService.findById(dto.getAgenceId());

	        agence.setDateDebutAbonnement(LocalDate.now());
	        agence.setDateFinAbonnement(LocalDate.now().plusMonths(1));
	        agence.setBloque(false);

	        agenceService.save(agence);
	    }

	    return ResponseEntity.ok("OK");
	}
	
}
