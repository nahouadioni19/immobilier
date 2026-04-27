package com.app.controller.common;

import java.time.LocalDate;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// 🔹 ton UserPrincipal
import com.app.security.UserPrincipal;
import com.app.entities.administration.Agence;
// 🔹 ton exception personnalisée
import com.app.exceptions.AbonnementExpireException;


@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public CustomAuthenticationProvider(UserDetailsService userDetailsService,
                                        PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

        System.out.println("🔥 CUSTOM AUTH PROVIDER ACTIF");
        
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserPrincipal user = (UserPrincipal) userDetailsService.loadUserByUsername(username);

        // =========================
        // 🔐 CHECK PASSWORD
        // =========================
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Bad credentials");
        }

        // =========================
        // 🔥 CHECK AGENCE / ABONNEMENT
        // =========================
        Agence agence = user.getUtilisateur().getAgence();

        if (agence != null) {

            // ❌ AGENCE BLOQUÉE
            if (Boolean.TRUE.equals(agence.getBloque())) {
                throw new LockedException("Agence bloquée");
            }

            // ❌ ABONNEMENT EXPIRÉ
            if (agence.getDateFinAbonnement() != null &&
                LocalDate.now().isAfter(agence.getDateFinAbonnement())) {

                throw new AbonnementExpireException();
            }
        }

        // =========================
        // ✅ AUTH OK
        // =========================
        return new UsernamePasswordAuthenticationToken(
                user,
                password,
                user.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

/*@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public CustomAuthenticationProvider(UserDetailsService userDetailsService,
                                        PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
    	
    	System.out.println("🔥 CUSTOM AUTH PROVIDER ACTIF");
    	
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserPrincipal user = (UserPrincipal) userDetailsService.loadUserByUsername(username);

        // 🔐 Vérification mot de passe
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Bad credentials");
        }

        // 🔥 ICI TON CHECK
        if (user.getUtilisateur().getAgence() != null) {

            if (Boolean.TRUE.equals(user.getUtilisateur().getAgence().getBloque())) {
                throw new LockedException("Agence bloquée");
            }

            if (user.getUtilisateur().getAgence().getDateFinAbonnement() != null &&
                LocalDate.now().isAfter(user.getUtilisateur().getAgence().getDateFinAbonnement())) {

             //   throw new AbonnementExpireException("ABONNEMENT_EXPIRE");
            }
        }
        
        return new UsernamePasswordAuthenticationToken(
                user,
                password,
                user.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}*/