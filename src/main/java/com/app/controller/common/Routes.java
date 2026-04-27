package com.app.controller.common;

public class Routes {
    private Routes() {
    }

    public static final String ROUTE_LOGIN = "/login";
    public static final String ROUTE_QUIT = "/quit";
    public static final String ROUTE_SUCCES_LOGIN = "/succes-forward-login";
    public static final String ROUTE_LOGOUT = "/sign-out";
    public static final String ROUTE_EXPIRED = "/expired";
    public static final String ROUTE_ACCESS_DENIED = "/error/access-denied";
    public static final String ROUTE_HOME = "/";
    public static final String REDIRECT_HOME = "redirect:/";
  //  public static final String REDIRECT_SELECTION_ROLE_URL = "redirect:/selection-role";
    
    public static final String ROUTE_BAILLEUR = "/bailleurs";
    public static final String ROUTE_LOCATAIRE = "/locataires";
    public static final String ROUTE_IMMEUB = "/patrimoines";
    public static final String ROUTE_BAIL = "/bails";
    public static final String ROUTE_ROLE = "/roles";
    public static final String ROUTE_TYPEROLE = "/type-roles";
    public static final String ROUTE_UTILISATEUR = "/utilisateurs";
    public static final String ROUTE_CARNET = "/carnets";
    
    public static final String ROUTE_LIST = "/index";
    public static final String ROUTE_CREATE = "/create";
    public static final String ROUTE_EDIT = "{id}/edit";
    public static final String ROUTE_DELETE = "{id}/delete";
    public static final String ROUTE_VIEW = "{id}/view";
    public static final String ROUTE_AJAX = "/ajax";
    public static final String ROUTE_REDIRECT_FIRST_CONNEXION_URL = "redirect:/administration/utilisateur/change-password";
    public static final String REDIRECT_ACCESS_DENIED_URL = "redirect:/error/access-denied";

    public static final String ROUTE_ACCESS_CONCURRENT = "/access-concurrent";
    public static final String ROUTE_NO_ROLE = "/no-role";
    public static final String REDIRECT_NO_ROLE = "redirect:/no-role";

    public static final String ROUTE_AJAX_POST = "/ajax-post";
    public static final String ROUTE_REDIRECT_ERROR_500 = "redirect:/error/500";
}
