package com.app.controller;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.app.controller.common.Pages;
import com.app.controller.common.Routes;
import com.app.utils.Constants;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LoginController {
	
	private final HttpSession httpSession;
	
	@GetMapping("/test")
    @ResponseBody
    public String test() {
        return "OK";
    }
	
    @GetMapping(Routes.ROUTE_LOGIN)
    public String signIn(Model model, HttpServletRequest request, @RequestParam Map<String, String> params) {
        handlerAuthenticationError(model, request, params);
        return Pages.PAGE_LOGIN;
    }


    private void handlerAuthenticationError(Model model, HttpServletRequest request, Map<String, String> params) {
        Exception exCatch = null;
        final String MSG_ERREUR_LOGIN = "msgErreurLogin";
        final String MSG_BLANK = "label.blank";
        String messageKey = null;

        if (params != null && params.keySet().contains("error"))
            exCatch = (Exception) request.getSession(false).getAttribute("SPRING_SECURITY_LAST_EXCEPTION");

        if (exCatch instanceof BadCredentialsException)
            messageKey = "login.error.message";
        if (exCatch instanceof SessionAuthenticationException)
            messageKey = "login.concurrent.user.exceed.error.message";

        model.addAttribute("showError", !StringUtils.isBlank(messageKey));
        if (StringUtils.isBlank(messageKey))
            messageKey = MSG_BLANK;

        model.addAttribute(MSG_ERREUR_LOGIN, messageKey);

    }

    @RequestMapping(value = Routes.ROUTE_QUIT, method = { RequestMethod.GET, RequestMethod.POST })
    public String quit(Model model) {
        httpSession.removeAttribute(Constants.APP_CREDENTIALS);
        httpSession.invalidate();
        return "redirect:/"+Routes.ROUTE_LOGIN;
    }
}