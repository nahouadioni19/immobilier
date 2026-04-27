/*package com.app.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.app.entities.administration.Utilisateur;
import com.app.repositories.administration.UtilisateurRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        // 1️⃣ Récupérer l'utilisateur connecté
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Utilisateur user = principal.getUtilisateur();

        // 2️⃣ Obtenir l'IP du client
        String ip = getClientIp(request);

        // 3️⃣ Mettre à jour IP et date de connexion
        user.setIpConnexion(ip);
        user.setLastConnexionDate(LocalDateTime.now());

        utilisateurRepository.save(user); // ✅ sauvegarde en base

        // 4️⃣ Redirection après login
        // Tu peux rediriger vers la page demandée avant login si tu utilises Spring Security SavedRequest
        String redirectUrl = (String) request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");
        if (redirectUrl == null) {
            redirectUrl = "/dashboard"; // fallback
        }
        response.sendRedirect(redirectUrl);
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isEmpty()) {
            return xfHeader.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}*/
