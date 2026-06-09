package com.app.controller.administration;

import com.app.controller.common.Routes;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.app.dto.AgenceDTO;
import com.app.dto.AgenceForm;
import com.app.dto.BailleurDTO;
import com.app.entities.administration.Agence;
import com.app.security.UserPrincipal;
import com.app.service.administration.AgenceService;

@Controller
@RequestMapping(Routes.ROUTE_AGENCE)
public class AgenceController {

	private final AgenceService service;
	    
    public AgenceController(AgenceService service) {
		this.service = service;
	}
    
    
    @GetMapping
    public String listAgences(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            Model model) {

        Page<AgenceDTO> agencesPage = service.search(keyword,PageRequest.of(page, 8));

        model.addAttribute("agencesPage", agencesPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);

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

	    return "redirect:"+Routes.ROUTE_AGENCE;
	}
	
	/*@GetMapping(value = "/api/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
	public Page<Agence> search(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 8) Pageable pageable) {

        return service.search(keyword, pageable);
    }*/
	
	@GetMapping(value = "/api/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> search(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 8) Pageable pageable) {

        Page<AgenceDTO> page =
                service.search(keyword, pageable);
        
        System.out.println("TOTAL PAGES = " + page.getTotalPages()); // 👈 AJOUTE ÇA

        return Map.of(
                "content", page.getContent(),
                "number", page.getNumber(),
                "size", page.getSize(),
                "totalElements", page.getTotalElements(),
                "totalPages", page.getTotalPages(),
                "last", page.isLast(),
                "first", page.isFirst()
        );
    }
}