package com.app.controller.administration;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.app.controller.common.Routes;

@Controller
public class ErrorController {

    @GetMapping(Routes.ROUTE_ACCESS_DENIED)
    public String accessDenied() {
        return "error/403"; // renvoie error/403.html dans templates
    }
}
