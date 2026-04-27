package com.app.controller.administration;

import java.security.Principal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.controller.common.Routes;
import com.app.controller.common.SetupPage;
import com.app.dto.AssignationDTO;
import com.app.dto.PasswordForm;
import com.app.dto.UtilisateurDTO;
import com.app.entities.administration.Assignation;
import com.app.entities.administration.Utilisateur;
import com.app.enums.Titre;
import com.app.mapper.UtilisateurMapper;
import com.app.service.administration.RoleService;
import com.app.service.administration.UtilisateurService;
import com.app.service.common.PaginationService;
import com.app.utils.Constants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping(Routes.ROUTE_UTILISATEUR)
@RequiredArgsConstructor
public class UtilisateurController {

    private final UtilisateurService service;
    private final RoleService roleService;
    private final SetupPage setup;
    private final PaginationService paginationService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
	private MessageSource messageSource;
    
    // -----------------------
    // LISTE
    // -----------------------
    @GetMapping
    public String listUtilisateurs(Model model, 
                                   @RequestParam(defaultValue = "0") int page, 
                                   HttpServletRequest request) {
        Page<Utilisateur> utilisateursPage = paginationService.getPage(service::findAll, page, 8);
        
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("utilisateursPage", utilisateursPage);
        model.addAttribute("utilisateurs", utilisateursPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", utilisateursPage.getTotalPages());
        
        return "administration/utilisateur/list";
    }

    // -----------------------
    // FORMULAIRE DE CREATION
    // -----------------------
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        UtilisateurDTO dto = new UtilisateurDTO();
        dto.getAssignations().add(new AssignationDTO()); // au moins 1 assignation vide
        model.addAttribute("utilisateur", dto);
        model.addAttribute("roles", roleService.findAllLight()); // ✅ version DTO
        model.addAttribute("listTitre", Titre.values());
        return "administration/utilisateur/form";
    }

    // -----------------------
    // FORMULAIRE DE MODIFICATION
    // -----------------------
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") int id, Model model) {
        Utilisateur user = service.findByIdWithAssignations(id)
                                  .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : " + id));
        UtilisateurDTO dto = UtilisateurMapper.toDTO(user);
        model.addAttribute("utilisateur", dto);
        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("listTitre", Titre.values());
        return "administration/utilisateur/form";
    }

    // -----------------------
    // ENREGISTREMENT
    // -----------------------
    
	/*
	 * @PostMapping("/save") public String saveUtilisateur(@ModelAttribute
	 * UtilisateurDTO dto, RedirectAttributes redirectAttrs) {
	 * 
	 * boolean isNew = dto.getId() == null;
	 * 
	 * Utilisateur user = service.findByIdWithAssignations(dto.getId())
	 * .orElseGet(Utilisateur::new);
	 * 
	 * UtilisateurMapper.updateEntity(user, dto);
	 * 
	 * if (isNew) {
	 * user.setPassword(passwordEncoder.encode(Constants.DEFAULT_PASSWORD)); }
	 * 
	 * // ========================= // 🔗 Assignations // =========================
	 * user.getAssignations().clear();
	 * 
	 * if (dto.getAssignations() != null) { dto.getAssignations().stream()
	 * .filter(adto -> adto.getRoleId() != null) .forEach(adto -> {
	 * 
	 * Assignation assign = new Assignation();
	 * 
	 * assign.setRole(roleService.findById(adto.getRoleId()) .orElseThrow(() -> new
	 * IllegalArgumentException("Rôle introuvable : " + adto.getRoleId())));
	 * 
	 * assign.setDateDebut( adto.getDateDebut() != null ? adto.getDateDebut() :
	 * LocalDate.now() );
	 * 
	 * assign.setDateFin( adto.getDateFin() != null ? adto.getDateFin() :
	 * LocalDate.of(9999, 12, 31) );
	 * 
	 * assign.setUtilisateur(user); //assign.setAgence(getCurrentAgence()); // 🔐
	 * SaaS
	 * 
	 * user.getAssignations().add(assign); }); }
	 * 
	 * service.save(user);
	 * 
	 * String successMessage = messageSource.getMessage( isNew ?
	 * "success.enregistrement" : "success.modification", null,
	 * LocaleContextHolder.getLocale() );
	 * 
	 * redirectAttrs.addFlashAttribute("successMessage", successMessage);
	 * 
	 * return "redirect:" + Routes.ROUTE_UTILISATEUR; }
	 */
    
    @PostMapping("/save")
    public String saveUtilisateur(@ModelAttribute UtilisateurDTO dto, RedirectAttributes redirectAttrs) {
        boolean isNew = dto.getId() == null;
        Utilisateur user = isNew 
                ? new Utilisateur() 
                : service.findByIdWithAssignations(dto.getId()).orElse(new Utilisateur());

        UtilisateurMapper.updateEntity(user, dto);

        if (isNew) {
            user.setPassword(passwordEncoder.encode(Constants.DEFAULT_PASSWORD));
        }

        user.getAssignations().clear();
        if (dto.getAssignations() != null) {
            dto.getAssignations().stream()
                .filter(adto -> adto.getRoleId() != null)
                .forEach(adto -> {
                    Assignation assign = new Assignation();
                    assign.setRole(roleService.findById(adto.getRoleId())
                                   .orElseThrow(() -> new IllegalArgumentException(
                                       "Rôle introuvable : " + adto.getRoleId())));
                    assign.setDateDebut(adto.getDateDebut() != null ? adto.getDateDebut() : LocalDate.now());
                    assign.setDateFin(adto.getDateFin() != null ? adto.getDateFin() : LocalDate.of(9999, 12, 31));
                    assign.setUtilisateur(user);
                    user.getAssignations().add(assign);
                });
        }

        service.save(user);

	    String successMessage = messageSource.getMessage(
	            isNew ? "success.enregistrement" : "success.modification",
	            null,
	            LocaleContextHolder.getLocale()
	    );
	  
    	redirectAttrs.addFlashAttribute("successMessage", successMessage);
    	
        return "redirect:" + Routes.ROUTE_UTILISATEUR;
    }

    // -----------------------
    // SUPPRESSION
    // -----------------------
    @GetMapping("/delete/{id}")
    public String deleteUtilisateur(@PathVariable("id") int id, RedirectAttributes redirectAttrs) {
        service.delete(id);
        redirectAttrs.addFlashAttribute("successMessage", "Suppression effectuée avec succès !");
        return "redirect:" + Routes.ROUTE_UTILISATEUR;
    }
    
    @GetMapping("/reinitialise/{id}")
    public String reinitialiserMotDePasse(@PathVariable("id") Integer userId, RedirectAttributes redirectAttrs) {
       
        // Appel au service pour réinitialiser le mot de passe
        boolean success = service.reinitialiserMotDePasse(userId, Constants.DEFAULT_PASSWORD);

        if (success) {
            redirectAttrs.addFlashAttribute("successMessage", "Mot de passe réinitialisé avec succès !");
        } else {
            redirectAttrs.addFlashAttribute("errorMessage", "Impossible de réinitialiser le mot de passe.");
        }

        return "redirect:" + Routes.ROUTE_UTILISATEUR;
    }
    
    @GetMapping("/change-password")
	public String showChangePasswordForm(Model model) {
	    model.addAttribute("passwordForm", new PasswordForm());
	    return "administration/utilisateur/change-password";
	}
        
    @PostMapping("/change-password")
    public String changePassword(@ModelAttribute("passwordForm") @Valid PasswordForm form,
                                 BindingResult result,
                                 Principal principal,
                                 RedirectAttributes redirectAttrs) {

        if (result.hasErrors()) {
            redirectAttrs.addFlashAttribute("errorMessage", "Formulaire invalide");
            return "redirect:/utilisateurs/change-password";
        }

        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            redirectAttrs.addFlashAttribute("errorMessage", "Les mots de passe ne correspondent pas");
            return "redirect:/utilisateurs/change-password";
        }

        boolean updated = service.updatePassword(
                principal.getName(),
                form.getCurrentPassword(),
                form.getNewPassword()
        );

        if (updated) {
            redirectAttrs.addFlashAttribute("successMessage", "Mot de passe modifié avec succès");
        } else {
            redirectAttrs.addFlashAttribute("errorMessage", "Mot de passe actuel incorrect");
        }

        return "redirect:/utilisateurs/change-password";
    }
    
    
    @GetMapping("/desactiver/{id}")
    public String desactiver(@PathVariable Integer id) {

        service.desactiverCompte(id);

        return "redirect:/utilisateurs";
    }


    @GetMapping("/activer/{id}")
    public String activer(@PathVariable Integer id) {

        service.activerCompte(id);

        return "redirect:/utilisateurs";
    }

}
