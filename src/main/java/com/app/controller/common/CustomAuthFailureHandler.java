package com.app.controller.common;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.app.exceptions.AbonnementExpireException;


@Component
public class CustomAuthFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException {

        Throwable cause = exception;
        
        System.out.println("TYPE RECU = " + exception.getClass());
        exception.printStackTrace();

        // 🔥 remonter toute la chaîne d'exception
        while (cause != null) {

            if (cause instanceof AbonnementExpireException) {
                response.sendRedirect(request.getContextPath() + "/paiement/expired");
                return;
            }

            if (cause instanceof LockedException) {
                response.sendRedirect(request.getContextPath() + "/login?error=blocked");
                return;
            }

            cause = cause.getCause();
        }

        response.sendRedirect(request.getContextPath() + "/login?error=true");
    }
}

/*@Component
public class CustomAuthFailureHandler implements AuthenticationFailureHandler {
    
	@Override
	public void onAuthenticationFailure(HttpServletRequest request,
	                                    HttpServletResponse response,
	                                    AuthenticationException exception)
	        throws IOException {

	    if (exception instanceof AbonnementExpireException) {
	        response.sendRedirect(request.getContextPath() + "/paiement/expired");
	        return;
	    }

	    if (exception instanceof LockedException) {
	        response.sendRedirect(request.getContextPath() + "/login?error=blocked");
	        return;
	    }

	    response.sendRedirect(request.getContextPath() + "/login?error=true");
	}
    
}*/