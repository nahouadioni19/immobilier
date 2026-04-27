package com.app.controller.administration;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.dto.AgenceForm;
import com.app.entities.administration.Agence;
import com.app.entities.administration.Banque;
import com.app.entities.administration.Role;
import com.app.service.administration.AgenceService;
import com.app.service.common.PaginationService;
import com.app.service.recouvre.AppartementService;
import com.app.service.recouvre.BailService;
import com.app.service.recouvre.LocataireService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("agences")
public class AgenceController {

	private final AgenceService service;	
	private final PaginationService paginationService;
	    
    public AgenceController(AgenceService service, PaginationService paginationService) {
		this.service = service;
		this.paginationService = paginationService;
	}

    @GetMapping
	public String listBanques(Model model, @RequestParam(defaultValue = "0") int page, HttpServletRequest request) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String username = auth.getName();

		// Pagination via PaginationService
		Page<Agence> agencesPage = paginationService.getPage(service::findAll, page, 8);

		model.addAttribute("currentUri", request.getRequestURI());
		model.addAttribute("agencesPage", agencesPage);
		model.addAttribute("agences", agencesPage.getContent()); // la liste pour Thymeleaf
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", agencesPage.getTotalPages());

		return "agence/list";
	}
    
	@GetMapping("/create")
    public String showForm(Model model) {
        model.addAttribute("agenceForm", new AgenceForm());
        return "agence/form";
    }
	
	@GetMapping("/edit/{id}")
	public String showEditForm(@PathVariable("id") Integer id, Model model) {

	    Agence agence = service.findById(id);

	    // 🔥 Mapping ENTITÉ -> FORM
	    AgenceForm agenceForm = new AgenceForm();
	    agenceForm.setId(agence.getId());
	    agenceForm.setNom(agence.getNom());
	    agenceForm.setCode(agence.getCode());
	    agenceForm.setTelephone(agence.getTelephone());
	    agenceForm.setEmail(agence.getEmail());
	    agenceForm.setAdresse(agence.getAdresse());
	    agenceForm.setVille(agence.getVille());
	    agenceForm.setMontantAbonnement(agence.getMontantAbonnement());
	    agenceForm.setDateDebutAbonnement(agence.getDateDebutAbonnement());
	    agenceForm.setDateFinAbonnement(agence.getDateFinAbonnement());
	    agenceForm.setActif(agence.isActif());
	    agenceForm.setBloque(agence.getBloque());

	    model.addAttribute("agence", agence);
	    model.addAttribute("agenceForm", agenceForm);

	    return "agence/form";
	}

	@PostMapping("/save")
	public String save(@ModelAttribute AgenceForm form) {

	    Agence agence = (form.getId() != null)
	            ? service.findById(form.getId())
	            : new Agence();

	    agence.setNom(form.getNom());
	    agence.setCode(form.getCode());
	    agence.setTelephone(form.getTelephone());
	    agence.setEmail(form.getEmail());
	    agence.setAdresse(form.getAdresse());
	    agence.setVille(form.getVille());
	    agence.setMontantAbonnement(form.getMontantAbonnement());
	    agence.setDateDebutAbonnement(form.getDateDebutAbonnement());
	    agence.setDateFinAbonnement(form.getDateFinAbonnement());
	    agence.setBloque(Boolean.TRUE.equals(form.getBloque()));

	    service.save(agence);

	    return "redirect:/agences";
	}
}