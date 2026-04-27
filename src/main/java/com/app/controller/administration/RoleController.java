package com.app.controller.administration;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.controller.common.Routes;
import com.app.controller.common.SetupPage;
import com.app.entities.administration.Role;
import com.app.entities.administration.Typerole;
import com.app.service.administration.RoleService;
import com.app.service.administration.TyperoleService;
import com.app.service.common.PaginationService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(Routes.ROUTE_ROLE)
public class RoleController {

    private final RoleService service;
    private final TyperoleService typeRoleService;
    private final PaginationService paginationService;
    private final SetupPage setup;
    
    @Autowired
	private MessageSource messageSource;
    
    public RoleController(SetupPage setup, PaginationService paginationService, TyperoleService typeRoleService, RoleService service ) {
        this.service = service;
        this.typeRoleService = typeRoleService;
        this.setup = setup;
        this.paginationService = paginationService;
    }

 // LISTE
    @GetMapping
    public String listRoles(Model model, @RequestParam(defaultValue = "0") int page, HttpServletRequest request) {
    	
        Page<Role> rolesPage = paginationService.getPage(service::findAll, page, 8);

        model.addAttribute("rolesPage", rolesPage);
        model.addAttribute("roles", rolesPage.getContent()); // la liste pour Thymeleaf
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", rolesPage.getTotalPages());
        model.addAttribute("currentUri", request.getRequestURI());        
        
        return "administration/role/list"; // Thymeleaf template : bailleur/list.html
    }
    
    @GetMapping("/create")
    public String showBailForm(Model model) {
        model.addAttribute("role", new Role());
        model.addAttribute("typeRoles", typeRoleService.findAll());
        return "administration/role/form";
    }

	/*
	 * @PostMapping("/save") public String saveBail(@ModelAttribute Role role,
	 * RedirectAttributes redirectAttrs) { boolean isNew = (role.getId() == 0);
	 * service.save(role); // Récupération du message depuis messages.properties
	 * String successMessage = messageSource.getMessage( isNew ?
	 * "success.enregistrement" : "success.modification", null,
	 * LocaleContextHolder.getLocale() ); // ⚡ Flash attribute pour survivre au
	 * redirect redirectAttrs.addFlashAttribute("successMessage", successMessage);
	 * return "redirect:" + Routes.ROUTE_ROLE; }
	 */
    
    
    @PostMapping("/save")
   	public String saveRole(
   	        @ModelAttribute  Role role,
   	        RedirectAttributes redirectAttrs) {

   	    boolean isNew = (role.getId() == null);

   	    Role saved = service.saverole(role); // ⚠️ ID généré ici

   	    String successMessage = messageSource.getMessage(
   	            isNew ? "success.enregistrement" : "success.modification",
   	            null,
   	            LocaleContextHolder.getLocale()
   	    );

   	    redirectAttrs.addFlashAttribute("successMessage", successMessage);

   	 return "redirect:" + Routes.ROUTE_ROLE;
   	}
    
 // FORMULAIRE DE MODIFICATION
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {

        Role role = service.findById(id)
                .orElseThrow(() -> new RuntimeException("Rôle introuvable : " + id));

        model.addAttribute("role", role);
        model.addAttribute("typeRoles", typeRoleService.findAll());

        return "administration/role/form";
    }

    // SUPPRESSION
    @GetMapping("/delete/{id}")
    public String deleteBail(@PathVariable("id") int id) {
        service.delete(id);
        return "redirect:" + Routes.ROUTE_ROLE;
    }
}

