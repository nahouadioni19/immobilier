package com.app.security;


import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.app.entities.administration.Utilisateur;
import com.app.service.administration.UserService;

import java.io.IOException;

@Component
public class PasswordChangeFilter implements Filter {

    private final UserService userService;

    public PasswordChangeFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String uri = req.getRequestURI();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            Utilisateur user = userService.findByUsername(auth.getName());

            boolean isChangePasswordPage = uri.startsWith(req.getContextPath() + "/utilisateurs/change-password");

            if (userService.isDefaultPassword(user) &&
                !isChangePasswordPage &&
                !uri.equals(req.getContextPath() + "/logout")) {

                res.sendRedirect(req.getContextPath() + "/utilisateurs/change-password");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
