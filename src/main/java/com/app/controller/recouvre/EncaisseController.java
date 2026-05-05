package com.app.controller.recouvre;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.dto.EncaisseForm;
import com.app.dto.IdentificationProjection;
import com.app.entities.administration.Utilisateur;
import com.app.entities.recouvre.Encaisse;
import com.app.repositories.BailSelectProjection;
import com.app.repositories.EncaisseListDto;
import com.app.security.UserPrincipal;
import com.app.service.FileStorageService;
import com.app.service.administration.UtilisateurService;
import com.app.service.recouvre.BailService;
import com.app.service.recouvre.EncaisseService;
import com.app.service.recouvre.IdentificationService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("recouvrements")
public class EncaisseController {
	
	private final EncaisseService service;
	private final BailService bailService;
	//private final LoyannService loyannService;
	private final UtilisateurService utilisateurService;
	private final IdentificationService identificationService;
	private final FileStorageService fileStorageService;
	
	@Autowired
	private MessageSource messageSource;
	
	public EncaisseController(EncaisseService service,
				BailService bailService, UtilisateurService utilisateurService, 
				IdentificationService identificationService, FileStorageService fileStorageService) {
		
		this.service = service;
		this.bailService = bailService;
		this.utilisateurService = utilisateurService;
		this.identificationService = identificationService;
		this.fileStorageService = fileStorageService;
	}
	
	@GetMapping
    public String listEncaisses(
            @AuthenticationPrincipal UserPrincipal principal,
            Model model,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "agentId", required = false) Long agentId,
            @RequestParam(value = "keyword", required = false) String keyword,
            HttpServletRequest request) {

        int pageSize = 8;

        // Récupération filtrée avec pagination
        Page<EncaisseListDto> encaissesPage =
                service.findByUtilisateur(principal, agentId, keyword, PageRequest.of(page, pageSize));

        model.addAttribute("encaissesPage", encaissesPage);
        model.addAttribute("encaisses", encaissesPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", encaissesPage.getTotalPages());
        model.addAttribute("currentUri", request.getRequestURI());

        // Liste des agents pour le select
        String code = "RECOUV"; // ou autre code si nécessaire
        List<Utilisateur> utilisateurs = utilisateurService.findByAgentRecouvrement(code);
        model.addAttribute("utilisateurs", utilisateurs);

        // Garder les valeurs sélectionnées dans le formulaire
        model.addAttribute("selectedAgent", agentId);
        model.addAttribute("keyword", keyword);

        return "recouvrement/list"; // correspond à src/main/resources/templates/recouvrement/list.html
    }
	
	
	@GetMapping("/create")
	public String showCreateForm(@AuthenticationPrincipal UserPrincipal principal,
	                             Model model,
	                             @RequestParam(value = "agentId", required = false) Long agentId,
	                             @RequestParam(required = false) String keyword,
	                             @RequestParam(value = "id", required = false) Integer id) {

		Integer agenceId = principal.getUtilisateur().getAgence().getId();
	    // ✅ Création du DTO (formulaire uniquement)
	    EncaisseForm form = new EncaisseForm();

	    // ✅ Injecter utilisateur connecté (SANS passer par Thymeleaf)
	    if (principal != null && principal.getUtilisateur() != null) {
	    	Integer userId = principal.getUtilisateur().getId();
	        form.setUtilisateurId(userId);
	        
	        form.setFiltreAgentId(userId);
	        
	    }

	    // ✅ Charger les baux (logique métier)
	    chargerBaux(principal, agentId, model, keyword, id);

	    // ✅ Charger les quittances
	    List<IdentificationProjection> identifications =
	            identificationService.findIdentificationsNative(principal, null, agentId,agenceId);
	    model.addAttribute("identifications", identifications);

	    // ✅ IMPORTANT : UN SEUL objet pour le form
	    model.addAttribute("encaisse", form);

	    // ✅ Liste des agents recouvrement
	    String code = "RECOUV";
	    List<Utilisateur> utilisateurs = utilisateurService.findByAgentRecouvrement(code);
	    model.addAttribute("utilisateurs", utilisateurs);

	    // ✅ Agent sélectionné (utile pour JS / select)
	    model.addAttribute("selectedAgent", agentId);

	    // ✅ (OPTIONNEL MAIS RECOMMANDÉ) ID utilisateur pour Thymeleaf
	    model.addAttribute("currentUserId",
	            principal != null && principal.getUtilisateur() != null
	                    ? principal.getUtilisateur().getId()
	                    : null);

	    return "recouvrement/form";
	}
	
		
	@GetMapping("/edit/{id}")
	public String showEditForm(@AuthenticationPrincipal UserPrincipal principal,
	                           @PathVariable Integer id,
	                           Model model) {

	    Encaisse encaisse = service.findByIdRelations(id)
	            .orElseThrow(() -> new IllegalArgumentException("Encaisse introuvable"));

	    EncaisseForm form = new EncaisseForm();

	    // =========================
	    // 🔑 IDENTIFIANTS
	    // =========================
	    form.setId(encaisse.getId());
	    form.setVersion(encaisse.getVersion());

	    // =========================
	    // 🔗 RELATIONS → ID
	    // =========================
	    if (encaisse.getUtilisateur() != null) {
	        form.setUtilisateurId(encaisse.getUtilisateur().getId());
	    }

	    if (encaisse.getBail() != null) {
	        form.setBailId(encaisse.getBail().getId());
	    }

	    if (encaisse.getIdentification() != null) {
	        form.setIdentificationId(encaisse.getIdentification().getId());
	    }

	    // =========================
	    // 🧾 CHAMPS MÉTIER
	    // =========================
	    form.setEncDate(encaisse.getEncDate());
	    form.setEncMontant(encaisse.getEncMontant());
	    form.setEncloyer(encaisse.getEncloyer());
	    form.setEncArriere(encaisse.getEncArriere());
	    form.setEncPenalite(encaisse.getEncPenalite());
	    form.setEnctotal(encaisse.getEnctotal());
	    form.setEncDeb(encaisse.getEncDeb());
	    form.setEncFin(encaisse.getEncFin());
	    form.setEncMode(encaisse.getEncMode());
	    form.setEncNumChq(encaisse.getEncNumChq());
	    form.setStatut(encaisse.getStatut());

	    // 🔥 IMPORTANT (garder le fichier existant)
	    form.setChequePath(encaisse.getChequePath());

	    model.addAttribute("encaisse", form);

	    // =========================
	    // 👥 LISTE AGENTS
	    // =========================
	    List<Utilisateur> utilisateurs = utilisateurService.findByAgentRecouvrement("RECOUV");
	    model.addAttribute("utilisateurs", utilisateurs);

	    // =========================
	    // 🎯 AGENT SÉLECTIONNÉ
	    // =========================
	    Integer selectedAgent = (encaisse.getUtilisateur() != null)
	            ? encaisse.getUtilisateur().getId()
	            : null;

	    form.setFiltreAgentId(selectedAgent);
	    model.addAttribute("selectedAgent", selectedAgent);

	    // =========================
	    // 🏠 INIT BAIL (Select2)
	    // =========================
	    if (encaisse.getBail() != null) {

	        model.addAttribute("initBailId", encaisse.getBail().getId());

	        String bailText = encaisse.getBail().getLocataire().getNom() + " "
	                + encaisse.getBail().getLocataire().getPrenom()
	                + " | " + encaisse.getBail().getAppartement().getNumAppart();

	        model.addAttribute("initBailText", bailText);
	    }

	    // =========================
	    // 📄 INIT QUITTANCE
	    // =========================
	    if (encaisse.getIdentification() != null) {
	        model.addAttribute("initIdentId", encaisse.getIdentification().getId());
	        model.addAttribute("initIdentText", encaisse.getIdentification().getIdeNumero());
	    }

	    // =========================
	    // 📎 CHEQUE (AFFICHAGE)
	    // =========================
	    String chequePath = encaisse.getChequePath();

	    model.addAttribute("chequeUrl",
	            (chequePath != null && !chequePath.isBlank())
	                    ? "/files/cheque/" + chequePath
	                    : null
	    );

	    return "recouvrement/form";
	}
	
		
	//
	private void chargerBaux(UserPrincipal principal, Long agentId, Model model, String keyword, Integer id) {

	    List<BailSelectProjection> bails = bailService.findBailDetailsNative(principal,agentId,keyword,id);

	    List<Map<String, Object>> bailData = bails.stream().map(b -> {
	        Map<String, Object> map = new HashMap<>();
	        map.put("id", b.getId());
	        map.put("locprenom", b.getLocprenom());
	        map.put("locnom", b.getLocnom());
	        map.put("loyer", b.getMontantloyer());
	        map.put("loctel", b.getLoctel());
	        map.put("bailibelle", b.getBailibelle());
	        map.put("usglibelle", b.getUsglibelle());
	        map.put("numero", b.getNumero());
	        map.put("lastpayment",
	                b.getDerniereDatePaiement() != null
	                        ? b.getDerniereDatePaiement()
	                        : b.getDateDebut());
	        return map;
	    }).toList();

	    model.addAttribute("bails", bailData);
	   
	}

	@GetMapping("/delete/{id}")
	public String deleteEncaisse(@PathVariable Integer id, RedirectAttributes redirectAttrs) {
		service.deleteById(id);
		
		redirectAttrs.addFlashAttribute("successMessage", "Suppression effectuée avec succès !");
		
		return "redirect:/recouvrements";
	}
	
	
	@PostMapping("/save")
	public String saveEncaisse(
	        @ModelAttribute EncaisseForm form,
	        @RequestParam(value = "chequeFile", required = false) MultipartFile chequeFile,
	        @RequestParam(value = "removeCheque", required = false) String removeCheque,
	        @AuthenticationPrincipal UserPrincipal principal,
	        RedirectAttributes redirectAttributes) {

	    try {

	        // =========================
	        // 🔐 rôles
	        // =========================
	        boolean isDirec = principal.getAuthorities().stream()
	                .anyMatch(auth -> "ROLE_DIREC".equals(auth.getAuthority()));

	        boolean isRecouv = principal.getAuthorities().stream()
	                .anyMatch(auth -> "ROLE_RECOUV".equals(auth.getAuthority()));

	        // =========================
	        // 🔐 utilisateur (agent connecté)
	        // =========================
	        form.setUtilisateurId(principal.getUtilisateur().getId());
	        
	        if(isRecouv) {
	        	form.setFiltreAgentId(principal.getUtilisateur().getId());
	        }
	        // =========================
	        // 🔐 statut automatique
	        // =========================
	        form.setStatut(isDirec ? 1 : 0);

	        // =========================
	        // 📁 GESTION FICHIER CHEQUE
	        // =========================
	        
	        if ("true".equals(removeCheque)) {
	            form.setChequePath(null);
	        }
	        
	        if (chequeFile != null && !chequeFile.isEmpty()) {

	            // 🔒 validation + sauvegarde
	            String fileName = fileStorageService.saveFile(chequeFile);

	            // 🔥 on injecte dans le form
	            form.setChequePath(fileName);

	        } else {
	            // 🔁 CAS UPDATE : garder ancien fichier
	        	if (form.getId() != null && (chequeFile == null || chequeFile.isEmpty())) {
	        	    service.findById(form.getId())
	        	            .ifPresent(existing -> form.setChequePath(existing.getChequePath()));
	        	}
	        }

	        // =========================
	        // 💾 sauvegarde
	        // =========================
	        service.saveEncaissement(form);

	        String message = messageSource.getMessage(
	                form.getId() == null ? "success.enregistrement" : "success.modification",
	                null,
	                LocaleContextHolder.getLocale()
	        );

	        redirectAttributes.addFlashAttribute("successMessage", message);

	    } catch (IllegalArgumentException e) {
	        redirectAttributes.addFlashAttribute("error", e.getMessage());

	    } catch (SecurityException e) {
	        redirectAttributes.addFlashAttribute("error", "Accès refusé");

	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("error", "Erreur inattendue");
	        e.printStackTrace();
	    }

	    System.out.println("FILE = " + chequeFile);
	    System.out.println("IS EMPTY = " + (chequeFile == null ? "null" : chequeFile.isEmpty()));
	    System.out.println("CHEQUE PATH AVANT SAVE = " + form.getChequePath());
	    
	    return "redirect:/recouvrements";
	}
	
	
	private void chargerListes(@AuthenticationPrincipal UserPrincipal principal, 
								@RequestParam(required = false) String keyword, Model model) {
	  
	    // Charger les listes pour le formulaire
	    model.addAttribute("bails", bailService.findBailDetailsNative(principal,null,keyword,null));

	    String code = "RECOUV";
	    model.addAttribute("utilisateurs", utilisateurService.findByAgentRecouvrement(code));
	}

	@PostMapping("/transmettre/{id}")
	public String transmettreEncaisse(@PathVariable Integer id,
	                                 @AuthenticationPrincipal UserPrincipal principal,
	                                 RedirectAttributes redirectAttributes) {

	    try {

	        Encaisse encaisse = service.findById(id)
	                .orElseThrow(() -> new IllegalArgumentException("Encaisse introuvable"));

	        Integer agenceId = principal.getUtilisateur().getAgence().getId();

	        if (encaisse.getAgence() == null 
	                || !encaisse.getAgence().getId().equals(agenceId)) {
	            throw new SecurityException("Accès refusé");
	        }

	        encaisse.setStatut(1);

	        service.save(encaisse);

	        redirectAttributes.addFlashAttribute("successMessage",
	                "Encaissement transmis avec succès");

	    } catch (SecurityException e) {
	        redirectAttributes.addFlashAttribute("error", "Accès refusé");

	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("error", e.getMessage());
	    }

	    return "redirect:/recouvrements";
	}

		
	@PostMapping("/valider/{id}")
	public String validerPaiement(@PathVariable Integer id,
	                              @AuthenticationPrincipal UserPrincipal principal,
	                              RedirectAttributes redirectAttributes) {

	    try {

	        service.validerPaiement(id, principal.getUtilisateur());

	        redirectAttributes.addFlashAttribute("successMessage",
	                "Paiement validé avec succès");

	    } catch (SecurityException e) {
	        redirectAttributes.addFlashAttribute("error", "Accès refusé");

	    } catch (IllegalArgumentException e) {
	        redirectAttributes.addFlashAttribute("error", e.getMessage());

	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("error", "Erreur inattendue");
	        e.printStackTrace();
	    }

	    return "redirect:/recouvrements";
	}

}
