/*
 * package com.app.controller.administration;
 * 
 * import java.security.Principal;
 * 
 * import jakarta.validation.Valid;
 * 
 * import org.springframework.stereotype.Controller; import
 * org.springframework.ui.Model; import
 * org.springframework.validation.BindingResult; import
 * org.springframework.web.bind.annotation.GetMapping; import
 * org.springframework.web.bind.annotation.ModelAttribute; import
 * org.springframework.web.bind.annotation.PostMapping;
 * 
 * // Ton DTO import com.app.dto.PasswordForm;
 * 
 * // Ton service utilisateur //import com.app.service.UserService;
 * 
 * 
 * public class Snippet {
 * 
 * @GetMapping("/change-password") public String showChangePasswordForm(Model
 * model) { model.addAttribute("passwordForm", new PasswordForm()); return
 * "change-password"; }
 * 
 * @PostMapping("/change-password") public String
 * changePassword(@ModelAttribute("passwordForm") @Valid PasswordForm form,
 * BindingResult result, Model model, Principal principal) { if
 * (result.hasErrors()) { return "change-password"; }
 * 
 * if (!form.getNewPassword().equals(form.getConfirmPassword())) {
 * model.addAttribute("errorMessage", "Les mots de passe ne correspondent pas");
 * return "change-password"; }
 * 
 * // Logique pour vérifier le mot de passe actuel et mettre à jour // boolean
 * updated = userService.updatePassword(principal.getName(),
 * form.getCurrentPassword(), form.getNewPassword()); boolean updated =
 * UtilisateurService.updatePassword(principal.getName(),
 * form.getCurrentPassword(), form.getNewPassword()); if (updated) {
 * model.addAttribute("successMessage", "Mot de passe modifié avec succès"); }
 * else { model.addAttribute("errorMessage", "Mot de passe actuel incorrect"); }
 * 
 * return "change-password"; }
 * 
 * }
 * 
 */