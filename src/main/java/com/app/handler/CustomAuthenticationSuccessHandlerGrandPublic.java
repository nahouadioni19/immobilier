package com.app.handler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.app.entities.administration.Utilisateur;
import com.app.repositories.administration.UtilisateurRepository;
import com.app.util.IpUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationSuccessHandlerGrandPublic implements AuthenticationSuccessHandler {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();
        
        Optional<Utilisateur> optionalUtilisateur = utilisateurRepository.findByUsernameIgnoreCase(username);
        
        if (optionalUtilisateur != null) {
        	Utilisateur utilisateur = optionalUtilisateur.get();        	
        	String ipAddress = IpUtils.getClientIp(request); // adresse IP du client
        	
           // String ipAddress = request.getRemoteAddr(); // adresse IP du client
            utilisateur.setIpConnexion(ipAddress);
      //      utilisateur.setDateDerniereConnexion(LocalDateTime.now());
            utilisateurRepository.save(utilisateur);
        }

        response.sendRedirect("/home");
    }
}

