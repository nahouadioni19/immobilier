package com.app.service.security;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.entities.administration.Utilisateur;
import com.app.security.UserPrincipal;
import com.app.service.AgenceSecurityService;
import com.app.service.administration.UtilisateurService;
import com.app.utils.Constants;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UtilisateurService userService;
    private final HttpSession httpSession;
    private final PasswordEncoder passwordEncoder;
    private final AgenceSecurityService agenceSecurityService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Utilisateur utilisateur;
        List<GrantedAuthority> authorities;

        // 🔹 ADMIN TECHNIQUE (hors BDD)
        if (Constants.DEFAULT_USER_NAME.equalsIgnoreCase(username)) {

            utilisateur = createDefaultAdminUser();

            authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + Constants.ROLE_ADMIN),
                new SimpleGrantedAuthority("ROLE_APP_USER")
            );

        } else {

            // 🔹 Chargement utilisateur BDD
            utilisateur = userService.findByUsernameIgnoreCaseWithRoles(username)
                .orElseThrow(() ->
                    new UsernameNotFoundException("Utilisateur introuvable : " + username)
                );

            // 🔐 VALIDATIONS AVANT AUTHENTIFICATION
            if (!utilisateur.isEnabled()) {
                throw new DisabledException("Compte désactivé");
            }

            if (utilisateur.getAgence() != null) {

                if (Boolean.TRUE.equals(utilisateur.getAgence().getBloque())) {
                    throw new LockedException("Agence bloquée");
                }

                agenceSecurityService.checkAgenceActive(utilisateur.getAgence());
            }

            // 🔹 Rôles utilisateur
            /*authorities = utilisateur.getAssignations().stream()
                .filter(a -> Boolean.TRUE.equals(a.isCourant()))
                .map(a -> new SimpleGrantedAuthority("ROLE_" + a.getRole().getCode()))
                .collect(Collectors.toList());*/
            
            authorities = utilisateur.getAssignations().stream()
            	    .filter(a -> Boolean.TRUE.equals(a.isCourant()))
            	    .filter(a -> a.getRole() != null)
            	    .map(a -> new SimpleGrantedAuthority(
            	            "ROLE_" + a.getRole().getCode().toUpperCase()))
            	    .collect(Collectors.toList());

            // 🔹 Rôle par défaut si aucun rôle
            if (authorities.isEmpty()) {
                authorities = List.of(new SimpleGrantedAuthority("ROLE_APP_USER"));
            }
        }

        // 🔹 Création principal
        UserPrincipal principal = new UserPrincipal(utilisateur, authorities);

        //AJOUTER CE 25/06/2026 A 11:47
        utilisateur.getAssignations()
					        .stream()
					        .filter(a -> Boolean.TRUE.equals(a.isCourant()))
					        .findFirst()
					        .ifPresent(principal::setAssignationCourant);
        
        // 🔹 Stockage session (après validation complète)
        httpSession.setAttribute(Constants.APP_CREDENTIALS, principal);

        return principal;
    }

    /**
     * Création utilisateur admin technique (hors base de données)
     */
    private Utilisateur createDefaultAdminUser() {
        Utilisateur admin = new Utilisateur();
        admin.setUsername(Constants.DEFAULT_USER_NAME);
        admin.setPassword(passwordEncoder.encode(Constants.DEFAULT_USER_PASS));
        admin.setNom("ADMIN");
        admin.setPrenoms("TECHNIQUE");
        admin.setAssignations(new ArrayList<>());
        admin.setEnabled(true);
        return admin;
    }
}