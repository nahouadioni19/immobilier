package com.app.controller.administration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import com.app.controller.common.Routes;
import com.app.entities.administration.Banque;
import com.app.entities.administration.Typerole;
import com.app.entities.referentiel.Profession;
import com.app.service.administration.TyperoleService;
import com.app.service.common.PaginationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(Routes.ROUTE_TYPEROLE)
public class TyperoleController {

    private final TyperoleService service;
    private final PaginationService paginationService;
    
    @Autowired
	private MessageSource messageSource;
    
    public TyperoleController(TyperoleService service, PaginationService paginationService) {
		this.service = service;
		this.paginationService = paginationService;
	}
    
    @GetMapping
    public String listTypes(Model model, @RequestParam(defaultValue = "0") int page, HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // utilisateur connecté (si besoin affichage)

        // Pagination via PaginationService
        Page<Typerole> typesPage = paginationService.getPage(service::findAll, page, 8);

        model.addAttribute("typesPage", typesPage);
        model.addAttribute("types", typesPage.getContent()); // la liste pour Thymeleaf
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", typesPage.getTotalPages());
        model.addAttribute("currentUri", request.getRequestURI());   

        return "administration/type-role/list";
    }
    
    // 🔹 Formulaire d’ajout
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("typerole", new Typerole());
        return "administration/type-role/form"; // --> templates/administration/typeRoles/form.html
    }
    
    @PostMapping("/save")
	public String saveTyper(
	        @ModelAttribute  Typerole typerole,
	        RedirectAttributes redirectAttrs) {

	    boolean isNew = (typerole.getId() == null);

	    Typerole saved = service.saveTyperole(typerole); // ⚠️ ID généré ici

	    String successMessage = messageSource.getMessage(
	            isNew ? "success.enregistrement" : "success.modification",
	            null,
	            LocaleContextHolder.getLocale()
	    );

	    redirectAttrs.addFlashAttribute("successMessage", successMessage);

	    return "redirect:/type-roles?success";
	}
    
    
    
    // 🔹 Formulaire de modification
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        Typerole typeRole = service.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Type rôle non trouvé id=" + id));
        model.addAttribute("typerole", typeRole);
        return "administration/type-role/form";
    }

    // 🔹 Suppression
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
    	service.delete(id);
        return "redirect:/type-roles?deleted";
    }
}
