package com.app.service.security;

import java.time.LocalDate;
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
import com.app.exceptions.AbonnementExpireException;
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
                new SimpleGrantedAuthority("ROLE_" + Constants.ROLE_ADMIN), // ROLE_ADMIN
                new SimpleGrantedAuthority("ROLE_APP_USER")
            );

        } else {
            // 🔹 Utilisateur BDD avec assignations et rôles chargés
            utilisateur = userService.findByUsernameIgnoreCaseWithRoles(username)
                .orElseThrow(() ->
                    new UsernameNotFoundException("Utilisateur introuvable : " + username)
                );
            
	         // 🔐 CHECK AGENCE
	            if (utilisateur.getAgence() != null) {
	
	                if (Boolean.TRUE.equals(utilisateur.getAgence().getBloque())) {
	                    throw new LockedException("Agence bloquée");
	                }
	
	               /* if (utilisateur.getAgence().getDateFinAbonnement() != null &&
	                    LocalDate.now().isAfter(utilisateur.getAgence().getDateFinAbonnement())) {
	
	                   // throw new DisabledException("Abonnement expiré");
	                	//throw new AbonnementExpireException("ABONNEMENT_EXPIRE");
	                		                	
	                }*/
	            }
	            
	         // 🔐 ICI
	         //   agenceSecurityService.checkAgenceActive(utilisateur.getAgence());

            // 🔹 Rôles depuis la BDD
            authorities = utilisateur.getAssignations().stream()
                .filter(a -> a.isCourant()) // prendre seulement les assignations en cours
                .map(a -> new SimpleGrantedAuthority("ROLE_" + a.getRole().getCode()))
                .collect(Collectors.toList());

            // Si aucun rôle trouvé, on ajoute ROLE_APP_USER par défaut
            if (authorities.isEmpty()) {
                authorities = List.of(new SimpleGrantedAuthority("ROLE_APP_USER"));
            }
        }

        // 🔹 Création UserPrincipal
        UserPrincipal principal = new UserPrincipal(utilisateur, authorities);

        // 🔹 Stockage en session pour réutilisation côté app
        httpSession.setAttribute(Constants.APP_CREDENTIALS, principal);

        return principal;
    }

    private Utilisateur createDefaultAdminUser() {
        Utilisateur admin = new Utilisateur();
        admin.setUsername(Constants.DEFAULT_USER_NAME);
        admin.setPassword(passwordEncoder.encode(Constants.DEFAULT_USER_PASS));
        admin.setNom("ADMIN");
        admin.setPrenoms("TECHNIQUE");
        admin.setAssignations(new ArrayList<>());
        return admin;
    }
}