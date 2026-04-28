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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.dto.EncaisseForm;
import com.app.dto.IdentificationProjection;
import com.app.entities.administration.Utilisateur;
import com.app.entities.recouvre.Bail;
import com.app.entities.recouvre.Encaisse;
import com.app.repositories.BailSelectProjection;
import com.app.repositories.EncaisseListDto;
import com.app.security.UserPrincipal;
import com.app.service.administration.UtilisateurService;
import com.app.service.recouvre.BailService;
import com.app.service.recouvre.EncaisseService;
import com.app.service.recouvre.IdentificationService;
import com.app.service.recouvre.LoyannService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("recouvrements")
public class EncaisseController {
	
	private final EncaisseService service;
	private final BailService bailService;
	private final LoyannService loyannService;
	private final UtilisateurService utilisateurService;
	private final IdentificationService identificationService;
	
	@Autowired
	private MessageSource messageSource;
	
	public EncaisseController(EncaisseService service, /*LocataireService locataireService,*/
				BailService bailService, LoyannService loyannService, UtilisateurService utilisateurService, 
				IdentificationService identificationService) {
		
		this.service = service;
		this.bailService = bailService;
		this.loyannService = loyannService;
		this.utilisateurService = utilisateurService;
		this.identificationService = identificationService;
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

	    // ✅ Création du DTO (formulaire uniquement)
	    EncaisseForm form = new EncaisseForm();

	    // ✅ Injecter utilisateur connecté (SANS passer par Thymeleaf)
	    if (principal != null && principal.getUtilisateur() != null) {
	        form.setUtilisateurId(principal.getUtilisateur().getId());
	    }

	    // ✅ Charger les baux (logique métier)
	    chargerBaux(principal, agentId, model, keyword, id);

	    // ✅ Charger les quittances
	    List<IdentificationProjection> identifications =
	            identificationService.findIdentificationsNative(principal, null, agentId);
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
	            .orElseThrow(() -> new IllegalArgumentException("Encaisse introuvable")); //findById

	    EncaisseForm form = new EncaisseForm();

	    form.setId(encaisse.getId());
	    form.setVersion(encaisse.getVersion());

	    // 🔥 RELATIONS → ID
	    if (encaisse.getUtilisateur() != null) {
	        form.setUtilisateurId(encaisse.getUtilisateur().getId());
	    }

	    if (encaisse.getBail() != null) {
	        form.setBailId(encaisse.getBail().getId());
	    }

	    if (encaisse.getIdentification() != null) {
	        form.setIdentificationId(encaisse.getIdentification().getId());
	    }

	    // CHAMPS
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

	    model.addAttribute("encaisse", form);

	    // 🔥 LISTE AGENTS
	    List<Utilisateur> utilisateurs = utilisateurService.findByAgentRecouvrement("RECOUV");
	    model.addAttribute("utilisateurs", utilisateurs);

	    // 🔥 AGENT SÉLECTIONNÉ (CRITIQUE)
	    Integer selectedAgent = null;
	    if (encaisse.getUtilisateur() != null) {
	        selectedAgent = encaisse.getUtilisateur().getId();
	    }
	    model.addAttribute("selectedAgent", selectedAgent);

	    // 🔥 INIT BAIL (POUR SELECT2)
	    if (encaisse.getBail() != null) {
	        model.addAttribute("initBailId", encaisse.getBail().getId());

	        String bailText = encaisse.getBail().getLocataire().getNom() + " "
	                + encaisse.getBail().getLocataire().getPrenom()
	                + " | " + encaisse.getBail().getAppartement().getNumAppart();

	        model.addAttribute("initBailText", bailText);
	    }

	    // 🔥 INIT QUITTANCE
	    if (encaisse.getIdentification() != null) {
	        model.addAttribute("initIdentId", encaisse.getIdentification().getId());
	        model.addAttribute("initIdentText", encaisse.getIdentification().getIdeNumero());
	    }

	    return "recouvrement/form";
	}
	
	/*@GetMapping("/edit/{id}")
	public String showEditForm(@AuthenticationPrincipal UserPrincipal principal,
	                           @PathVariable Integer id,
	                           @RequestParam(value = "agentId", required = false) Long agentId,
	                           @RequestParam(required = false) String keyword,
	                           Model model) {

	    // 🔎 Charger entity
	    Encaisse encaisse = service.findById(id)
	            .orElseThrow(() -> new IllegalArgumentException("Encaisse introuvable"));

	    // 🔄 Conversion ENTITY → DTO
	    EncaisseForm form = new EncaisseForm();

	    form.setId(encaisse.getId());
	    form.setVersion(encaisse.getVersion());

	    // Relations → IDs
	    if (encaisse.getUtilisateur() != null) {
	        form.setUtilisateurId(encaisse.getUtilisateur().getId());
	    }

	    if (encaisse.getBail() != null) {
	        form.setBailId(encaisse.getBail().getId());
	    }

	    if (encaisse.getIdentification() != null) {
	        form.setIdentificationId(encaisse.getIdentification().getId());
	    }

	    // Champs simples
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

	    //  IMPORTANT : envoyer le DTO
	    model.addAttribute("encaisse", form);

	    // 🔎 charger données métier
	    chargerBaux(principal, agentId, model, keyword, id);
	    
	    

	 // ✅ Liste des agents recouvrement
	    String code = "RECOUV";
	    List<Utilisateur> utilisateurs = utilisateurService.findByAgentRecouvrement(code);
	    model.addAttribute("utilisateurs", utilisateurs);

	    // ✅ Agent sélectionné (utile pour JS / select)
	    model.addAttribute("selectedAgent", agentId);
	    
	    
	    
	    
	    List<IdentificationProjection> identifications =
	            identificationService.findIdentificationsNative(principal, id, agentId);
	    model.addAttribute("identifications", identifications);

	    return "recouvrement/form";
	}*/
	
		
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
	public String saveEncaisse(@ModelAttribute EncaisseForm form,
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
	        // 🔐 gestion utilisateur (CRITIQUE)
	        // =========================
	        if (isRecouv) {
	            // 🔥 un agent ne peut enregistrer que pour lui-même
	            form.setUtilisateurId(principal.getUtilisateur().getId());

	        } else {
	            // 🔥 ADMIN / DIREC → doivent choisir un agent
	            if (form.getUtilisateurId() == null || form.getUtilisateurId() == 9999999) {
	                throw new IllegalArgumentException("Agent de recouvrement obligatoire");
	            }
	        }

	        // =========================
	        // 🔐 statut automatique pour directeur
	        // =========================
	        if (isDirec) {
	            form.setStatut(1);
	        }

	        // =========================
	        // 💾 sauvegarde
	        // =========================
	        Encaisse saved = service.saveEncaissement(form);

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

	    return "redirect:/recouvrements";
	}
	
	/*@PostMapping("/save")
	public String saveEncaisse(@ModelAttribute EncaisseForm form,
	                           @AuthenticationPrincipal UserPrincipal principal,
	                           RedirectAttributes redirectAttributes) {

	    try {

	        // =========================
	        // 🔐 rôle directeur
	        // =========================
	        boolean isDirec = principal.getAuthorities().stream()
	                .anyMatch(auth -> "ROLE_DIREC".equals(auth.getAuthority()));

	        // =========================
	        // 🔐 statut injecté dans le form (propre)
	        // =========================
	        if (isDirec) {
	            form.setStatut(1);
	        }

	        // =========================
	        // 🔐 utilisateur connecté injecté
	        // =========================
	        form.setUtilisateurId(principal.getUtilisateur().getId());

	        // =========================
	        // 💾 appel service (TOUT est ici)
	        // =========================
	        Encaisse saved = service.saveEncaissement(form);

	        String message = messageSource.getMessage(
	                form.getId() == null ? "success.enregistrement" : "success.modification",
	                null,
	                LocaleContextHolder.getLocale()
	        );

	        redirectAttributes.addFlashAttribute("successMessage", message);

	    } catch (SecurityException e) {
	        redirectAttributes.addFlashAttribute("error", "Accès refusé");

	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("error", "Erreur inattendue");
	        e.printStackTrace();
	    }

	    return "redirect:/recouvrements";
	}*/
	
	/*@PostMapping("/save")
	public String saveEncaisse(@ModelAttribute Encaisse encaisse,
	                           @AuthenticationPrincipal UserPrincipal principal,
	                           Model model, RedirectAttributes redirectAttributes) {
		
		boolean isDirec = principal.getAuthorities().stream()
		        .anyMatch(auth -> "ROLE_DIREC".equals(auth.getAuthority()));
		
	    Utilisateur user = principal.getUtilisateur();
	    encaisse.setUtilisateur(user);

	    // 🔹 Hydrate le Bail si ID fourni
	    if (encaisse.getBail() != null && encaisse.getBail().getId() != null) {
	        Bail bail = bailService.findById(encaisse.getBail().getId())
	                .orElseThrow(() -> new RuntimeException("Bail introuvable"));
	        encaisse.setBail(bail);
	    } else {
	        encaisse.setBail(null); // ou lever exception si obligatoire
	    }

	    // 🔹 Initialisation sécurité pour Longs
	    encaisse.setEncMontant(encaisse.getEncMontant() != null ? encaisse.getEncMontant() : 0L);
	    encaisse.setEncPerdeb(encaisse.getEncPerdeb() != null ? encaisse.getEncPerdeb() : 0L);
	    encaisse.setEncAndeb(encaisse.getEncAndeb() != null ? encaisse.getEncAndeb() : 0L);
	    encaisse.setEnctotal(encaisse.getEnctotal() != null ? encaisse.getEnctotal() : 0L);
	    encaisse.setEncloyer(encaisse.getEncloyer() != null ? encaisse.getEncloyer() : 0L);
	    
	    if(isDirec) {
	    	encaisse.setStatut(1); // mettre le statut à 1
	    }
	    // 🔹 Sauvegarde
	    service.saveEncaissement(encaisse);

	    String successMessage = messageSource.getMessage(
	            encaisse.getId() == null ? "success.enregistrement" : "success.modification",
	            null,
	            LocaleContextHolder.getLocale()
	    );
	    redirectAttributes.addFlashAttribute("successMessage", successMessage);

	    return "redirect:/recouvrements";
	}*/
	

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
	
	/*@PostMapping("/transmettre/{id}")
	public String transmettreEncaisse(@ModelAttribute EncaisseForm form,@PathVariable Integer id) {
	    Encaisse encaisse = service.findById(id);
	    encaisse.setStatut(1); // mettre le statut à 1
	    service.saveEncaissement(encaisse);

	    return "redirect:/recouvrements"; // retour à la liste
	}*/
	
	/*@PostMapping("/valider/{id}")
    public String validerPaiement(@PathVariable("id") Integer id) {
        // 1. Récupérer l’encaissement
        Encaisse encaisse = service.findById(id);
        if (encaisse == null) {
            return "redirect:/recouvrements?error=notfound";
        }

        // 2. Récupérer les infos du bail et du montant payé
        Bail bail = encaisse.getBail();
        long montantPaye = encaisse.getEncMontant();
        long loyerMensuel = bail.getMontantLoyer();
        LocalDate dateDebut = bail.getDateFin();

        // 3. Répartir le paiement
        loyannService.repartirLoyer(bail, montantPaye, loyerMensuel, dateDebut);

        // 4. Mettre à jour l’encaissement (statut = validé)
        encaisse.setEncvalide(true);
        encaisse.setStatut(2);
        service.saveEncaissement(encaisse);

        return "redirect:/recouvrements"; // revenir à la liste
    }*/
	
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
