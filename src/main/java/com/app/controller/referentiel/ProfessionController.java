package com.app.controller.referentiel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.entities.recouvre.Bailleur;
import com.app.entities.referentiel.Profession;
import com.app.service.common.PaginationService;
import com.app.service.referentiel.ProfessionService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("professions")
public class ProfessionController {

	private final ProfessionService service;
	private final PaginationService paginationService;

	@Autowired
	private MessageSource messageSource;
	
	public ProfessionController(ProfessionService service, PaginationService paginationService) {
		this.service = service;
		this.paginationService = paginationService;
	}
	
	@GetMapping
    public String listProfessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            Model model) {

        Page<Profession> professionsPage = service.search(keyword,PageRequest.of(page, 8));

        model.addAttribute("professionsPage", professionsPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);

        return "referentiel/profession/list";
    }

	@GetMapping("/create")
	public String showCreateForm(Model model) {

		Profession profession = new Profession();

		model.addAttribute("profession", profession);

		return "referentiel/profession/form";
	}

	@PostMapping("/save")
	public String saveProfession(
	        @ModelAttribute Profession profession,
	        RedirectAttributes redirectAttrs) {

	    boolean isNew = (profession.getId() == null);

	    Profession saved = service.saveProf(profession); // ⚠️ ID généré ici

	    String successMessage = messageSource.getMessage(
	            isNew ? "success.enregistrement" : "success.modification",
	            null,
	            LocaleContextHolder.getLocale()
	    );

	    redirectAttrs.addFlashAttribute("successMessage", successMessage);

	    return "redirect:/professions";
	}

	@GetMapping("/edit/{id}")
	public String showEditForm(@PathVariable Integer id, Model model) {

		Profession profession = service.findById(id);

		model.addAttribute("profession", profession);

		return "referentiel/profession/form";
	}

	@GetMapping("/delete/{id}")
	public String deleteProfession(@PathVariable Integer id, RedirectAttributes redirectAttrs) {
		service.deleteById(id);

		redirectAttrs.addFlashAttribute("successMessage", "Suppression effectuée avec succès !");

		return "redirect:/professions";
	}

	@GetMapping(value = "/api/professions", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> searchProfession(@RequestParam String term) {

        Page<Profession> page = service.search(term, PageRequest.of(0, 50));

        List<Map<String, Object>> results = page.getContent().stream()
                .map(l -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", l.getId());
                    m.put("text", l.getLibelle());
                    m.put("libelle", l.getLibelle());   // ✅ AJOUTÉ                    
                    return m;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("results", results);

        return response;
    }
	
	@GetMapping(value = "/api/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page<Profession> search(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 8) Pageable pageable) {

        return service.search(keyword, pageable);
    }
}
