package com.app.controller.recouvre;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.app.controller.common.Routes;
import com.app.dto.CarnetDTO;
import com.app.dto.IdentificationProjection;
import com.app.entities.administration.Utilisateur;
import com.app.entities.recouvre.Carnet;
import com.app.repositories.BailSelectProjection;
import com.app.security.UserPrincipal;
import com.app.service.administration.UtilisateurService;
import com.app.service.common.PaginationService;
import com.app.service.recouvre.CarnetService;
import com.app.service.recouvre.IdentificationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RequestMapping(Routes.ROUTE_CARNET)
public class CarnetController {
	
	private final CarnetService service;
	private final UtilisateurService utilisateurService;
	private final IdentificationService identificationService;
	
	@Autowired
	private MessageSource messageSource;
	
	public CarnetController(CarnetService service, UtilisateurService utilisateurService, 
								IdentificationService identificationService) {
		
		this.service = service;
		this.utilisateurService = utilisateurService;
		this.identificationService = identificationService;
		
	}
	
	@GetMapping
	public String listCarnets( //@AuthenticationPrincipal UserPrincipal principal
	        Principal principal,
	        Model model,
	        @RequestParam(defaultValue = "0") int page,
	        HttpServletRequest request) {
	    
	    String username = principal.getName();

		List<Utilisateur> users = utilisateurService.findByUsername(username);

		if (users.isEmpty()) {
		    throw new IllegalArgumentException("Utilisateur introuvable : " + username);
		}

		// Si admin → plusieurs utilisateurs, si non admin → 1 utilisateur
		Utilisateur user = users.get(0);

	    // Pagination
	    int pageSize = 12;
	    Page<Carnet> carnetsPage = service.findByUtilisateur(user, PageRequest.of(page, pageSize));

	    // Model
	    model.addAttribute("carnetsPage", carnetsPage);
	    model.addAttribute("carnets", carnetsPage.getContent());
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", carnetsPage.getTotalPages());
	    model.addAttribute("currentUri", request.getRequestURI());

	    return "carnet/list";
	}
	
	
	@GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("carnetDto", new CarnetDTO());
        model.addAttribute("utilisateurs", utilisateurService.findAll());
        return "carnet/form";
    }

		    
	@PostMapping("/save")
    public String saveCarnet(@ModelAttribute("carnetDto") CarnetDTO dto, RedirectAttributes redirectAttributes, BindingResult result, Model model) {
        
		boolean isNew = (dto.getId() == null);
		
		if (dto.getCarNumDeb() != 0 && dto.getCarNumFin() != 0 && dto.getCarNumDeb() > dto.getCarNumFin()) {
            result.rejectValue("carNumDeb", "error.carNumDeb", "⚠️ Le numéro de début ne peut pas être supérieur au numéro de fin !");
        }

        // Vérifier si chevauchement avec un carnet déjà existant
        if (service.rangeOverlap(dto.getCarNumDeb(), dto.getCarNumFin())) {
            result.rejectValue("carNumDeb", "error.carNumDeb", "⚠️ Cette plage de numéros chevauche une autre déjà existante !");
        }

        if (result.hasErrors()) {
            model.addAttribute("utilisateurs", utilisateurService.findAll());
            return "carnet/form";
        }

        service.saveFromDto(dto);
        
     // Récupération du message depuis messages.properties
	    String successMessage = messageSource.getMessage(
	            isNew ? "success.enregistrement" : "success.modification",
	            null,
	            LocaleContextHolder.getLocale()
	    );
    	    // ⚡ Flash attribute pour survivre au redirect
        redirectAttributes.addFlashAttribute("successMessage", successMessage);

        return "redirect:" + Routes.ROUTE_CARNET;
    }


	@GetMapping("/active/{id}")
	public String activerCarnet(@PathVariable Integer id, Principal principal, RedirectAttributes redirectAttributes) {
		
	    service.activerCarnet(id, principal.getName());
	    
	    redirectAttributes.addFlashAttribute(
	            "successMessage",
	            "Carnet activérésilié avec succès"
	        );
	    
	    return "redirect:" + Routes.ROUTE_CARNET;
	}
		
	// FORMULAIRE DE MODIFICATION
	@GetMapping("/edit/{id}")
	public String showEditForm(@PathVariable("id") int id, Model model) {

	    // Récupérer le carnet depuis la base
	    Carnet carnet = service.findById(id)
	            .orElseThrow(() -> new IllegalArgumentException("Carnet introuvable : " + id));

	    // Convertir l'entité en DTO
	    CarnetDTO carnetDto = new CarnetDTO();
	    carnetDto.setId(carnet.getId());
	    carnetDto.setUtilisateurId(carnet.getUtilisateur() != null ? carnet.getUtilisateur().getId() : null);
	    carnetDto.setCarNumDeb(carnet.getCarNumDeb());
	    carnetDto.setCarNumFin(carnet.getCarNumFin());

	    // Ajouter le DTO au modèle
	    model.addAttribute("carnetDto", carnetDto);

	    // Ajouter la liste des utilisateurs pour le select
	    List<Utilisateur> utilisateurs = utilisateurService.findAll();
	    model.addAttribute("utilisateurs", utilisateurs);

	    return "carnet/form";
	}

    // SUPPRESSION
    @GetMapping("/delete/{id}")
    public String deleteBail(@PathVariable("id") int id, RedirectAttributes redirectAttrs) {
        service.delete(id);
        
        redirectAttrs.addFlashAttribute("successMessage", "Suppression effectuée avec succès !");
        
        return "redirect:" + Routes.ROUTE_CARNET;
    }

    @GetMapping(value = "/api/identifications", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<IdentificationProjection> getIdentifications(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) Integer encaisseId,
            @RequestParam(required = false) Long agentId) {

        return identificationService.findIdentificationsNative(principal, encaisseId,agentId);
    }

}
