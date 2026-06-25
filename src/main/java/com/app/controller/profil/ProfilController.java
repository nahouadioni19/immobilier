package com.app.controller.profil;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import com.app.entities.administration.Assignation;
import com.app.security.UserPrincipal;

import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

@Controller
@RequestMapping("/profil")
public class ProfilController {

    @PostMapping("/change")
    public String changeProfil(
            @RequestParam Integer assignationId,
            HttpServletRequest request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        UserPrincipal principal =
                (UserPrincipal) authentication.getPrincipal();

        Assignation assignation = principal.getUtilisateur()
                .getAssignations()
                .stream()
                .filter(a -> a.getId().equals(assignationId))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Profil introuvable"));

        // Profil actif
        principal.setAssignationCourant(assignation);

        // Nouvelles autorités
        List<GrantedAuthority> authorities =
                List.of(
                        new SimpleGrantedAuthority(
                                "ROLE_" + assignation.getRole().getCode()
                        )
                );

        Authentication newAuth =
                new UsernamePasswordAuthenticationToken(
                        principal,
                        authentication.getCredentials(),
                        authorities
                );

        SecurityContextHolder.getContext().setAuthentication(newAuth);
        
        request.getSession().setAttribute(
                "CURRENT_ASSIGNATION_ID",
                assignation.getId().longValue()
        );

        request.getSession().setAttribute(
                "CURRENT_ROLE",
                assignation.getRole().getLibelle());
        
        request.getSession()
        .setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );

        String referer = request.getHeader("Referer");

        return "redirect:" +
                (referer != null ? referer : "/");
    }
}