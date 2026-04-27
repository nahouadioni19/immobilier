/*
 * package com.app.controller.administration;
 * 
 * import java.security.Principal; import java.time.LocalDate;
 * 
 * import org.springframework.data.domain.Page; import
 * org.springframework.security.crypto.password.PasswordEncoder; import
 * org.springframework.stereotype.Controller; import
 * org.springframework.ui.Model; import
 * org.springframework.validation.BindingResult; import
 * org.springframework.web.bind.annotation.GetMapping; import
 * org.springframework.web.bind.annotation.ModelAttribute; import
 * org.springframework.web.bind.annotation.PathVariable; import
 * org.springframework.web.bind.annotation.PostMapping; import
 * org.springframework.web.bind.annotation.RequestMapping; import
 * org.springframework.web.bind.annotation.RequestParam; import
 * org.springframework.web.servlet.mvc.support.RedirectAttributes;
 * 
 * import com.app.controller.common.Routes; import
 * com.app.controller.common.SetupPage; import com.app.dto.AssignationDTO;
 * import com.app.dto.PasswordForm; import com.app.dto.UtilisateurDTO; import
 * com.app.entities.administration.Assignation; import
 * com.app.entities.administration.Utilisateur; import com.app.enums.Titre;
 * import com.app.mapper.UtilisateurMapper; import
 * com.app.service.administration.RoleService; import
 * com.app.service.administration.UserService; import
 * com.app.service.administration.UtilisateurService; import
 * com.app.service.common.PaginationService; import com.app.utils.Constants;
 * 
 * import jakarta.servlet.http.HttpServletRequest; import
 * jakarta.validation.Valid;
 * 
 * @Controller
 * 
 * @RequestMapping(Routes.ROUTE_UTILISATEUR) public class
 * UtilisateurControllerAncien {
 * 
 * private final UtilisateurService service; private final RoleService
 * roleService; private final PaginationService paginationService; private final
 * SetupPage setup; private final PasswordEncoder passwordEncoder; // private
 * final UserService userService;
 * 
 * public UtilisateurControllerAncien(UtilisateurService service, RoleService
 * roleService, SetupPage setup, PaginationService paginationService,
 * PasswordEncoder passwordEncoder) { this.service = service; this.roleService =
 * roleService; this.setup = setup; this.paginationService = paginationService;
 * this.passwordEncoder = passwordEncoder; // this.userService = userService; }
 * 
 * // LISTE
 * 
 * @GetMapping public String listUtilisateurs(Model
 * model, @RequestParam(defaultValue = "0") int page, HttpServletRequest
 * request) {
 * 
 * Page<Utilisateur> utilisateursPage =
 * paginationService.getPage(service::findAll, page, 8);
 * 
 * model.addAttribute("currentUri", request.getRequestURI());
 * model.addAttribute("utilisateursPage", utilisateursPage);
 * model.addAttribute("utilisateurs", utilisateursPage.getContent()); // la
 * liste pour Thymeleaf model.addAttribute("currentPage", page);
 * model.addAttribute("totalPages", utilisateursPage.getTotalPages());
 * 
 * return "administration/utilisateur/list"; // Thymeleaf template :
 * bailleur/list.html }
 * 
 * @GetMapping("/create") public String showBailForm(Model model) {
 * UtilisateurDTO dto = new UtilisateurDTO();
 * 
 * // ⚡ Crée au moins une assignation vide pour l'affichage du formulaire
 * dto.getAssignations().add(new AssignationDTO());
 * 
 * model.addAttribute("utilisateur", dto); model.addAttribute("roles",
 * roleService.findAll()); model.addAttribute("listTitre", Titre.values());
 * 
 * return "administration/utilisateur/form"; }
 * 
 * 
 * @PostMapping("/save") public String saveUtilisateur(@ModelAttribute
 * UtilisateurDTO dto, RedirectAttributes redirectAttrs) { boolean isNew =
 * (dto.getId() == null); // Si ID existe → édition, sinon création Utilisateur
 * user = dto.getId() != null ? service.findById(dto.getId()).orElse(new
 * Utilisateur()) : new Utilisateur();
 * 
 * // ----------------------- // Champs utilisateur // -----------------------
 * user.setNom(dto.getNom()); user.setPrenoms(dto.getPrenoms());
 * user.setUsername(dto.getUsername()); user.setMatricule(dto.getMatricule());
 * user.setTelephone(dto.getTelephone()); user.setEmail(dto.getEmail());
 * user.setTitre(dto.getTitre());
 * 
 * // Mot de passe par défaut si création if (dto.getId() == null) {
 * user.setPassword(passwordEncoder.encode(Constants.DEFAULT_PASSWORD)); }
 * 
 * // ----------------------- // Assignations (rôles) // -----------------------
 * user.getAssignations().clear();
 * 
 * if (dto.getAssignations() != null) { dto.getAssignations().stream()
 * .filter(adto -> adto.getRoleId() != null) // ignore les assignations sans
 * rôle .forEach(adto -> { Assignation assign = new Assignation();
 * 
 * // Dates par défaut assign.setDateDebut(adto.getDateDebut() != null ?
 * adto.getDateDebut() : LocalDate.now()); assign.setDateFin(adto.getDateFin()
 * != null ? adto.getDateFin() : LocalDate.of(9999, 12, 31));
 * 
 * // Rôle obligatoire assign.setRole(roleService.findById(adto.getRoleId())
 * .orElseThrow(() -> new IllegalArgumentException( "Rôle introuvable : " +
 * adto.getRoleId())));
 * 
 * assign.setUtilisateur(user); user.getAssignations().add(assign); }); }
 * 
 * // ----------------------- // Sauvegarde // -----------------------
 * service.save(user);
 * 
 * String successMessage = isNew ? "Utilisateur enregistré avec succès !" :
 * "Utilisateur modifié avec succès !";
 * 
 * // ⚡ Flash attribute pour survivre au redirect
 * redirectAttrs.addFlashAttribute("successMessage", successMessage);
 * 
 * return "redirect:" + Routes.ROUTE_UTILISATEUR; }
 * 
 * // FORMULAIRE DE MODIFICATION
 * 
 * @GetMapping("/edit/{id}") public String showEditForm(@PathVariable("id") int
 * id, Model model) { Utilisateur user = service.findByIdWithAssignations(id)
 * .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable : "
 * + id));
 * 
 * UtilisateurDTO dto = UtilisateurMapper.toDTO(user);
 * 
 * model.addAttribute("utilisateur", dto); model.addAttribute("roles",
 * roleService.findAll()); model.addAttribute("listTitre", Titre.values());
 * 
 * return "administration/utilisateur/form"; }
 * 
 * // SUPPRESSION
 * 
 * @GetMapping("/delete/{id}") public String deleteBail(@PathVariable("id") int
 * id, RedirectAttributes redirectAttrs) { service.delete(id);
 * 
 * redirectAttrs.addFlashAttribute("successMessage",
 * "Suppression effectuée avec succès !");
 * 
 * return "redirect:" + Routes.ROUTE_UTILISATEUR; }
 * 
 * @GetMapping("/reinitialise/{id}") public String
 * reinitialiserMotDePasse(@PathVariable("id") Integer userId,
 * RedirectAttributes redirectAttrs) {
 * 
 * // Appel au service pour réinitialiser le mot de passe boolean success =
 * service.reinitialiserMotDePasse(userId, Constants.DEFAULT_PASSWORD);
 * 
 * if (success) { redirectAttrs.addFlashAttribute("successMessage",
 * "Mot de passe réinitialisé avec succès !"); } else {
 * redirectAttrs.addFlashAttribute("errorMessage",
 * "Impossible de réinitialiser le mot de passe."); }
 * 
 * return "redirect:" + Routes.ROUTE_UTILISATEUR; }
 * 
 * @GetMapping("/change-password") public String showChangePasswordForm(Model
 * model) { model.addAttribute("passwordForm", new PasswordForm()); return
 * "administration/utilisateur/change-password"; }
 * 
 * @PostMapping("/change-password") public String
 * changePassword(@ModelAttribute("passwordForm") @Valid PasswordForm form,
 * BindingResult result, Model model, Principal principal, RedirectAttributes
 * redirectAttrs) { if (result.hasErrors()) { return
 * "administration/utilisateur/change-password"; }
 * 
 * if (!form.getNewPassword().equals(form.getConfirmPassword())) {
 * model.addAttribute("errorMessage", "Les mots de passe ne correspondent pas");
 * return "administration/utilisateur/change-password"; }
 * 
 * // Logique pour vérifier le mot de passe actuel et mettre à jour // boolean
 * updated = userService.updatePassword(principal.getName(),
 * form.getCurrentPassword(), form.getNewPassword()); boolean updated =
 * service.updatePassword(principal.getName(), form.getCurrentPassword(),
 * form.getNewPassword()); if (updated) { model.addAttribute("successMessage",
 * "Mot de passe modifié avec succès");
 * redirectAttrs.addFlashAttribute("successMessage",
 * "Mot de passe modifié avec succès"); } else {
 * redirectAttrs.addFlashAttribute("errorMessage",
 * "Mot de passe actuel incorrect"); }
 * 
 * return "administration/utilisateur/change-password"; } }
 * 
 */