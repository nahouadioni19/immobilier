package com.app.security;

import com.app.entities.administration.Utilisateur;
import com.app.service.administration.UserService;
import com.app.repositories.administration.UtilisateurRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAuthenticationSuccessHandler
        implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final UtilisateurRepository utilisateurRepository;

    public CustomAuthenticationSuccessHandler(
            UserService userService,
            UtilisateurRepository utilisateurRepository
    ) {
        this.userService = userService;
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        String username = authentication.getName();

        try {
            // 🔹 Charger l'utilisateur attaché à Hibernate avec ses rôles
            Utilisateur user = userService.findByUsernameWithRoles(username);

            if (user != null) {
                updateConnexion(user, getClientIp(request));

                // Redirection si mot de passe par défaut
                if (userService.isDefaultPassword(user)) {
                    response.sendRedirect(
                            request.getContextPath() + "/utilisateurs/change-password"
                    );
                    return;
                }
            }

        } catch (RuntimeException e) {
            // Pour les utilisateurs non trouvés, continuer vers la home
            // ou afficher un message si nécessaire
        }

        // Redirection vers la page d'accueil
        response.sendRedirect(request.getContextPath() + "/");
    }

    // ===============================
    // IP client (proxy compatible)
    // ===============================
    private String getClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isEmpty()) {
            return xf.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    // ===============================
    // 🔹 Mise à jour de l'utilisateur dans une transaction
    // ===============================
    @Transactional
    protected void updateConnexion(Utilisateur user, String ip) {
        user.setIpConnexion(ip);
        user.setLastConnexionDate(LocalDateTime.now());
        utilisateurRepository.saveAndFlush(user);
    }
}

/*package com.app.security;

import com.app.entities.administration.Utilisateur;
import com.app.repositories.administration.UtilisateurRepository;
import com.app.service.administration.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAuthenticationSuccessHandler
        implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final UtilisateurRepository utilisateurRepository;

    public CustomAuthenticationSuccessHandler(
            UserService userService,
            UtilisateurRepository utilisateurRepository
    ) {
        this.userService = userService;
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        // 🔹 Username connecté
        String username = authentication.getName();

        // 🔹 Charger l'utilisateur depuis la DB (attaché Hibernate)
       // Utilisateur user = userService.findByUsername(username);
        Utilisateur user = userService.findByUsernameWithRoles(username);
     // ✅ CAS 1 : utilisateur DB
        // ============================
        if (user != null) {

            String ip = getClientIp(request);

            user.setIpConnexion(ip);
            user.setLastConnexionDate(LocalDateTime.now());

            utilisateurRepository.saveAndFlush(user);

            // Redirection mot de passe
            if (userService.isDefaultPassword(user)) {

                response.sendRedirect(
                    request.getContextPath() + "/utilisateurs/change-password"
                );
                return;
            }
        }

        response.sendRedirect(request.getContextPath() + "/");
    }

    // ===============================
    // IP client (proxy compatible)
    // ===============================
    private String getClientIp(HttpServletRequest request) {

        String xf = request.getHeader("X-Forwarded-For");

        if (xf != null && !xf.isEmpty()) {
            return xf.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}*/
