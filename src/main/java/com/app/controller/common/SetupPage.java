package com.app.controller.common;

import static com.app.utils.Constants.*;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.HandlerMapping;

import com.app.enums.ActionMenu;
import com.app.entities.administration.Assignation;
import com.app.entities.administration.Role;
import com.app.entities.administration.Utilisateur;
import com.app.entities.referentiel.Exercice;
import com.app.pojo.FileUploadedPojo;
import com.app.security.UserPrincipal;
import com.app.session.PageData;
import com.app.utils.Constants;
import com.app.utils.FUtils;
import com.app.utils.JUtils;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SetupPage {

    @Value("${server.servlet.context-path}")
    private String appContext;

    private final HttpSession httpSession;
    private final PageData pages;

    public final List<String> allMenuAction = Arrays.stream(ActionMenu.values())
            .map(ActionMenu::name)
            .collect(Collectors.toList());

    protected final Map<String, String> map2 = allMenuAction.stream()
            .collect(Collectors.toMap(action -> action, String::toLowerCase));

    // ================== PAGES ==================

    public void createPage(Model model, Map<String, String> options, boolean isPostMethod) {
        model.addAttribute(FEATURE_TITLE, options.get(FEATURE_TITLE));
        model.addAttribute(PAGE_TITLE, options.get("pageTitle.new"));
        model.addAttribute(ACTION, "./create");
        model.addAttribute(LABEL_BUTTON_ACTION, "label.btn.enregistrer");
        model.addAttribute(MESSAGE_CONFIRMATION, "label.confirmation.message.enregistrer");
        model.addAttribute(RESULTAT_MESSAGE, "label.resultat.message.enregistrer");
        model.addAttribute(RETURN_LINK, "./index");

        model.addAttribute(OPERATION_SUCCESS, (isPostMethod ? "SUCCESS" : "NONE"));
        model.addAttribute(MODEL_ATTRIBUTE_IS_UPDATE, false);
        model.addAttribute(MODEL_ATTRIBUTE_IS_VIEW, false);
        baseCommon(model, options);
    }

    public void editPage(Model model, Map<String, String> options, boolean isPostMethod) {
        model.addAttribute(FEATURE_TITLE, options.get(FEATURE_TITLE));
        model.addAttribute(PAGE_TITLE, options.get("pageTitle.edit"));
        model.addAttribute(ACTION, "./edit");
        model.addAttribute(MESSAGE_CONFIRMATION, "label.confirmation.message.modifier");
        model.addAttribute(RESULTAT_MESSAGE, "label.resultat.message.modifier");
        model.addAttribute(LABEL_BUTTON_ACTION, "label.btn.modifier");
        model.addAttribute(RETURN_LINK, "../index");
        model.addAttribute(OPERATION_SUCCESS, (isPostMethod ? MODEL_ATTRIBUTE_SUCCESS : MODEL_ATTRIBUTE_NONE));
        model.addAttribute(MODEL_ATTRIBUTE_IS_UPDATE, true);
        model.addAttribute(MODEL_ATTRIBUTE_IS_VIEW, false);
        baseCommon(model, options);
    }

    public void viewPage(Model model, Map<String, String> options) {
        model.addAttribute(FEATURE_TITLE, options.get(FEATURE_TITLE));
        model.addAttribute(PAGE_TITLE, options.get("pageTitle.view"));
        model.addAttribute(ACTION, "./view");
        model.addAttribute(MESSAGE_CONFIRMATION, BLANK_MESSAGE);
        model.addAttribute(RESULTAT_MESSAGE, BLANK_MESSAGE);
        model.addAttribute(LABEL_BUTTON_ACTION, BLANK_MESSAGE);
        model.addAttribute(RETURN_LINK, "../index");
        model.addAttribute(OPERATION_SUCCESS, MODEL_ATTRIBUTE_NONE);
        model.addAttribute(MODEL_ATTRIBUTE_IS_UPDATE, false);
        model.addAttribute(MODEL_ATTRIBUTE_IS_VIEW, true);
        baseCommon(model, options);
    }

    public void listPage(Model model, Map<String, String> options, boolean isDelete) {
        model.addAttribute(FEATURE_TITLE, options.get("featureTitle"));
        model.addAttribute(PAGE_TITLE, options.get("pageListTitle"));
        model.addAttribute(ACTION, "./create");
        model.addAttribute(MESSAGE_CONFIRMATION, "label.confirmation.message.supprimer");
        model.addAttribute(RESULTAT_MESSAGE, "label.resultat.message.supprimer");
        model.addAttribute(LABEL_BUTTON_ACTION, "label.btn.new");
        model.addAttribute(OPERATION_SUCCESS, isDelete ? MODEL_ATTRIBUTE_SUCCESS : MODEL_ATTRIBUTE_NONE);
        baseCommon(model, options);
    }

    public void baseCommon(Model model, Map<String, String> options) {
        model.addAttribute(Constants.CURR_PAGE, getCurrentPage(options.get("featureLink")));
        model.addAttribute(Constants.GRP_PAGE, getTopPage(options.get("featureLink")));
        model.addAttribute(VIEW_PATH, options.get(VIEW_PATH));
        model.addAttribute("EXERCICE", getUserPrincipal().getExercice());
        model.addAttribute("IS_TRANSMISSIBLE", "true".equals(options.get("isTransmissible")));
        model.addAttribute("isViewObs", "true".equals(options.get("isViewObs")));

        model.addAttribute("TAB_EN_ATTENTE", options.get("tabEnAttente"));
        model.addAttribute("TAB_VALIDE", options.get("tabValide"));
        model.addAttribute("TAB_TRANSMIS", options.get("tabTransmis"));
        model.addAttribute("noVersion", false);
        model.addAttribute("isCheck", false);
        allCommon(model);
    }

    public void allCommon(Model model) {
        model.addAttribute(Constants.USER_PRINCIPAL, getUserPrincipal());
        model.addAttribute("hasMultipleRole", getUserPrincipal().hasMultipleRole());
        model.addAttribute("isObservateur", getUserPrincipal().isActeurObservateur());
        model.addAttribute("context", appContext);

        pageSuffix(model);
    }

    public void pageSuffix(Model model) {
        model.addAttribute("pageSuffix", "/index");
    }

    // ================== UPLOAD ==================

    public void upload(Model model, HttpServletRequest request, Map<String, String> params, MultipartFile[] files, String categorie) {
        boolean hasError = false;
        String errMsg = "";
        String filePath = "";

        try {
            String key = StringUtils.trimToEmpty(params.get("key"));

            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile mfile = multipartRequest.getFile("file");

            FileUploadedPojo uploadFile = FUtils.uploadFile(mfile);
            filePath = uploadFile.getFilePath();

            Map<String, FileUploadedPojo> uploadedFiles = getPages().get(categorie, Constants.MODEL_ATTRIBUTE_UPLOADED_FILES);

            if (uploadedFiles == null)
                uploadedFiles = new HashMap<>();
            uploadedFiles.put(key, uploadFile);
            getPages().doStack(getPages().getData(), categorie, Constants.MODEL_ATTRIBUTE_UPLOADED_FILES, uploadedFiles);
        } catch (Exception e) {
            hasError = true;
            errMsg = e.getMessage();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("hasError", hasError);
        response.put("errMsg", errMsg);
        response.put("filePath", filePath);

        JUtils.setModelAttribute(model, "DATA", JUtils.toJSON(response));
        JUtils.setModelAttribute(model, "ACTION", "upload");
    }

    // ================== UTILITAIRES ==================

    public String getCurrentPage(String currPage) {
        return currPage.replace(Constants.SEPARATEUR_URL, "_").substring(1);
    }

    private String getTopPage(String currPage) {
        return currPage.substring(1).split(Constants.SEPARATEUR_URL)[0];
    }

    private boolean isShowPage(HttpServletRequest request, String page) {
        String appUrl = String.valueOf(request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE));
        return (StringUtils.isNotBlank(appUrl) && appUrl.endsWith(page));
    }

    public boolean isPage(HttpServletRequest request, String page) {
        String appUrl = String.valueOf(request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE));
        return (StringUtils.isNotBlank(appUrl) && appUrl.startsWith(page));
    }

    @SuppressWarnings("all")
    public String getIdPathVariable(HttpServletRequest request) {
        Map<String, Object> pathVariables = (Map<String, Object>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        return String.valueOf(pathVariables.get("id"));
    }

    public int getIdValue(HttpServletRequest request) {
        String idPathVariable = getIdPathVariable(request);
        return StringUtils.isNotBlank(idPathVariable) ? Integer.parseInt(idPathVariable) : 0;
    }

    public boolean isShowCreate(HttpServletRequest request) { return isShowPage(request, APP_ACTION_CREATE); }
    public boolean isShowEdit(HttpServletRequest request) { return isShowPage(request, APP_ACTION_EDIT); }
    public boolean isShowValide(HttpServletRequest request) { return isShowPage(request, APP_ACTION_AJAX); }
    public boolean isShowView(HttpServletRequest request) { return isShowPage(request, APP_ACTION_VIEW); }
    protected boolean isDeleteAction(HttpServletRequest request) { return isShowPage(request, APP_ACTION_DELETE); }
    protected boolean isShowIndex(HttpServletRequest request) { return isShowPage(request, APP_ACTION_LIST); }

    // ================== SÉCURISATION USER ==================

    public UserPrincipal getUserPrincipal() {
        UserPrincipal principal = (UserPrincipal) httpSession.getAttribute(APP_CREDENTIALS);
        if (principal == null) {
            principal = createEmptyUserPrincipal();
            httpSession.setAttribute(APP_CREDENTIALS, principal);
        }
        return principal;
    }

    private UserPrincipal createEmptyUserPrincipal() {
        Utilisateur dummyUser = new Utilisateur();
        dummyUser.setUsername(Constants.DEFAULT_USER_NAME); // "ANONYMOUS"
        dummyUser.setPassword(""); // facultatif

        UserPrincipal dummyPrincipal = new UserPrincipal(dummyUser, List.of()); // pas d'autorité
        dummyPrincipal.setLoggedIn(false);
        dummyPrincipal.setAssignations(List.of());
        dummyPrincipal.setAssignationCourant(null);
        dummyPrincipal.setExercice(null);
        dummyPrincipal.setExercices(List.of());
        dummyPrincipal.hasMultipleRole(false);
        dummyPrincipal.hasRole(false);

        return dummyPrincipal;
    }

    public void setUserPrincipalSession(UserPrincipal userConnected) {
        httpSession.setAttribute(APP_CREDENTIALS, userConnected);
    }

    public boolean isConnected() {
        UserPrincipal principal = getUserPrincipal();
        return principal != null && principal.isLoggedIn();
    }

    public int getModifierParValue() {
        Assignation assignation = getAssignationCourant();
        if (assignation == null && getUserPrincipal().isDefaultUser())
            return 0;
        else if (assignation != null)
            return assignation.getId();
        else
            return 0;
    }

    public Assignation getAssignationCourant() {
        UserPrincipal principal = getUserPrincipal();
        if (principal == null || principal.getAssignationCourant() == null)
            return null;
        return principal.getAssignationCourant();
    }

    public Role getRoleCourant() {
        Assignation assignation = getAssignationCourant();
        return (assignation != null) ? assignation.getRole() : null;
    }

    public Exercice getExercice() {
        UserPrincipal principal = getUserPrincipal();
        return (principal != null) ? principal.getExercice() : null;
    }

    public List<String> getUserMenuActions(String viewBase, String currAction) {
        if (getUserPrincipal().isDefaultUser()) return allMenuAction;

        var listAction = getUserPrincipal().getMenuKeyToActions().get(getCurrentPage(viewBase));
        if (listAction == null) return List.of();

        Predicate<String> predicate = null;

        switch (currAction) {
            case APP_ACTION_LIST -> predicate = x -> List.of(APP_ACTION_DELETE, APP_ACTION_VIEW.toUpperCase(),
                    APP_ACTION_EDIT.toUpperCase(), APP_ACTION_CREATE.toUpperCase(),
                    APP_ACTION_PRINT.toUpperCase(), APP_ACTION_PRINT_GLOBAL.toUpperCase()).contains(x);
            case APP_ACTION_CREATE -> predicate = x -> x.equalsIgnoreCase(APP_ACTION_CREATE);
            case APP_ACTION_EDIT -> predicate = x -> x.equalsIgnoreCase(APP_ACTION_EDIT);
            case APP_ACTION_VIEW -> predicate = x -> List.of(APP_ACTION_VIEW, APP_ACTION_EDIT.toUpperCase(),
                    APP_ACTION_DELETE.toUpperCase(), APP_ACTION_PRINT.toUpperCase()).contains(x);
        }

        return (predicate != null) ? listAction.stream().filter(predicate).toList() : List.of();
    }

    public boolean hasAccess(Model model, List<String> userMenuActions, boolean isTransmissible) {
        Map<String, Boolean> map = allMenuAction.stream()
                .collect(Collectors.toMap(action -> "CAN_" + action, userMenuActions::contains));

        if (isTransmissible) {
            boolean canValidate = map.getOrDefault("CAN_VALIDATE", false)
                    || map.getOrDefault("CAN_REJECT", false)
                    || map.getOrDefault("CAN_RETURN", false)
                    || map.getOrDefault("CAN_DIFFER", false);
            boolean canTransmit = map.getOrDefault("CAN_TRANSMIT", false);
            map.put("CAN_TRANSMIT_OR_VALIDATE", canValidate || canTransmit);
            map.put("CAN_TRANSMIT_AND_VALIDATE", canValidate && canTransmit);
        }

        model.addAllAttributes(map);
        model.addAllAttributes(map2);

        return getUserPrincipal().isDefaultUser() || map.values().contains(true);
    }

    public PageData getPages() { return this.pages; }

    public String getContext() { return this.appContext; }

}



/*package com.app.controller.common;

import static com.app.utils.Constants.ACTION;
import static com.app.utils.Constants.BLANK_MESSAGE;
import static com.app.utils.Constants.FEATURE_TITLE;
import static com.app.utils.Constants.LABEL_BUTTON_ACTION;
import static com.app.utils.Constants.MESSAGE_CONFIRMATION;
import static com.app.utils.Constants.MODEL_ATTRIBUTE_IS_UPDATE;
import static com.app.utils.Constants.MODEL_ATTRIBUTE_IS_VIEW;
import static com.app.utils.Constants.MODEL_ATTRIBUTE_NONE;
import static com.app.utils.Constants.MODEL_ATTRIBUTE_SUCCESS;
import static com.app.utils.Constants.OPERATION_SUCCESS;
import static com.app.utils.Constants.PAGE_TITLE;
import static com.app.utils.Constants.RESULTAT_MESSAGE;
import static com.app.utils.Constants.RETURN_LINK;
import static com.app.utils.Constants.VIEW_PATH;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.HandlerMapping;
import com.app.enums.ActionMenu;
import com.app.entities.administration.Assignation;
import  com.app.entities.administration.Role;
import  com.app.entities.referentiel.Exercice;
import  com.app.pojo.FileUploadedPojo;
import  com.app.security.UserPrincipal;
import  com.app.session.PageData;
import  com.app.utils.Constants;
import  com.app.utils.FUtils;
import  com.app.utils.JUtils;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SetupPage {

    @Value("${server.servlet.context-path}")
    private String appContext;

    private final HttpSession httpSession;
    private final PageData pages;

    public final List<String> allMenuAction = Arrays.stream(ActionMenu.values()).map(ActionMenu::name).collect(Collectors.toList());
    protected final Map<String, String> map2 = allMenuAction.stream().collect(Collectors.toMap(action -> action, String::toLowerCase));

    public void createPage(Model model, Map<String, String> options, boolean isPostMethod) {
        model.addAttribute(FEATURE_TITLE, options.get(FEATURE_TITLE));
        model.addAttribute(PAGE_TITLE, options.get("pageTitle.new"));
        model.addAttribute(ACTION, "./create");
        model.addAttribute(LABEL_BUTTON_ACTION, "label.btn.enregistrer");
        model.addAttribute(MESSAGE_CONFIRMATION, "label.confirmation.message.enregistrer");
        model.addAttribute(RESULTAT_MESSAGE, "label.resultat.message.enregistrer");
        model.addAttribute(RETURN_LINK, "./index");

        model.addAttribute(OPERATION_SUCCESS, (isPostMethod ? "SUCCESS" : "NONE"));
        model.addAttribute(MODEL_ATTRIBUTE_IS_UPDATE, false);
        model.addAttribute(MODEL_ATTRIBUTE_IS_VIEW, false);
        baseCommon(model, options);
    }

    public void editPage(Model model, Map<String, String> options, boolean isPostMethod) {
        model.addAttribute(FEATURE_TITLE, options.get(FEATURE_TITLE));
        model.addAttribute(PAGE_TITLE, options.get("pageTitle.edit"));
        model.addAttribute(ACTION, "./edit");
        model.addAttribute(MESSAGE_CONFIRMATION, "label.confirmation.message.modifier");
        model.addAttribute(RESULTAT_MESSAGE, "label.resultat.message.modifier");
        model.addAttribute(LABEL_BUTTON_ACTION, "label.btn.modifier");
        model.addAttribute(RETURN_LINK, "../index");
        model.addAttribute(OPERATION_SUCCESS, (isPostMethod ? MODEL_ATTRIBUTE_SUCCESS : MODEL_ATTRIBUTE_NONE));
        model.addAttribute(MODEL_ATTRIBUTE_IS_UPDATE, true);
        model.addAttribute(MODEL_ATTRIBUTE_IS_VIEW, false);
        baseCommon(model, options);
    }

    public void viewPage(Model model, Map<String, String> options) {
        model.addAttribute(FEATURE_TITLE, options.get(FEATURE_TITLE));
        model.addAttribute(PAGE_TITLE, options.get("pageTitle.view"));
        model.addAttribute(ACTION, "./view");
        model.addAttribute(MESSAGE_CONFIRMATION, BLANK_MESSAGE);
        model.addAttribute(RESULTAT_MESSAGE, BLANK_MESSAGE);
        model.addAttribute(LABEL_BUTTON_ACTION, BLANK_MESSAGE);
        model.addAttribute(RETURN_LINK, "../index");
        model.addAttribute(OPERATION_SUCCESS, MODEL_ATTRIBUTE_NONE);
        model.addAttribute(MODEL_ATTRIBUTE_IS_UPDATE, false);
        model.addAttribute(MODEL_ATTRIBUTE_IS_VIEW, true);
        baseCommon(model, options);
    }

    public void listPage(Model model, Map<String, String> options, boolean isDelete) {
        model.addAttribute(FEATURE_TITLE, options.get("featureTitle"));
        model.addAttribute(PAGE_TITLE, options.get("pageListTitle"));
        model.addAttribute(ACTION, "./create");
        model.addAttribute(MESSAGE_CONFIRMATION, "label.confirmation.message.supprimer");
        model.addAttribute(RESULTAT_MESSAGE, "label.resultat.message.supprimer");
        model.addAttribute(LABEL_BUTTON_ACTION, "label.btn.new");
        model.addAttribute(OPERATION_SUCCESS, isDelete ? MODEL_ATTRIBUTE_SUCCESS : MODEL_ATTRIBUTE_NONE);
        baseCommon(model, options);
    }

    public void baseCommon(Model model, Map<String, String> options) {
        model.addAttribute(Constants.CURR_PAGE, getCurrentPage(options.get("featureLink")));
        model.addAttribute(Constants.GRP_PAGE, getTopPage(options.get("featureLink")));
        model.addAttribute(VIEW_PATH, options.get(VIEW_PATH));
        model.addAttribute("EXERCICE", getUserPrincipal().getExercice());
        model.addAttribute("IS_TRANSMISSIBLE", "true".equals(options.get("isTransmissible")));
        model.addAttribute("isViewObs", "true".equals(options.get("isViewObs")));

        model.addAttribute("TAB_EN_ATTENTE", options.get("tabEnAttente"));
        model.addAttribute("TAB_VALIDE", options.get("tabValide"));
        model.addAttribute("TAB_TRANSMIS", options.get("tabTransmis"));
        model.addAttribute("noVersion", false);
        model.addAttribute("isCheck", false);
        allCommon(model);
    }

    public void allCommon(Model model) {
        model.addAttribute(Constants.USER_PRINCIPAL, getUserPrincipal());
        model.addAttribute("hasMultipleRole", getUserPrincipal().hasMultipleRole());
        model.addAttribute("isObservateur", getUserPrincipal().isActeurObservateur());
        model.addAttribute("context", appContext);

        pageSuffix(model);
    }

    public void pageSuffix(Model model) {
        model.addAttribute("pageSuffix", "/index");
    }

    
    public void upload(Model model, HttpServletRequest request, Map<String, String> params, MultipartFile[] files, String categorie) {
        boolean hasError = false;
        String errMsg = "";
        String filePath = "";

        try {
            String key = StringUtils.trimToEmpty(params.get("key"));

            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile mfile = multipartRequest.getFile("file");

            FileUploadedPojo uploadFile = FUtils.uploadFile(mfile);
            filePath = uploadFile.getFilePath();

            Map<String, FileUploadedPojo> uploadedFiles = getPages().get(categorie, Constants.MODEL_ATTRIBUTE_UPLOADED_FILES);

            JUtils.getModelAttribute(model, Constants.MODEL_ATTRIBUTE_UPLOADED_FILES);
            if (uploadedFiles == null)
                uploadedFiles = new HashMap<>();
            uploadedFiles.put(key, uploadFile);
            getPages().doStack(getPages().getData(), categorie, Constants.MODEL_ATTRIBUTE_UPLOADED_FILES, uploadedFiles);
        } catch (Exception e) {
            hasError = true;
            errMsg = e.getMessage();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("hasError", hasError);
        response.put("errMsg", errMsg);
        response.put("filePath", filePath);

        JUtils.setModelAttribute(model, "DATA", JUtils.toJSON(response));
        JUtils.setModelAttribute(model, "ACTION", "upload");
    }

    public String getCurrentPage(String currPage) {
        return currPage.replace(Constants.SEPARATEUR_URL, "_").substring(1);
    }

    private String getTopPage(String currPage) {
        return currPage.substring(1).split(Constants.SEPARATEUR_URL)[0];
    }

    private boolean isShowPage(HttpServletRequest request, String page) {
        String appUrl = String.valueOf(request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE));
        return (StringUtils.isNotBlank(appUrl) && appUrl.endsWith(page));
    }

    public boolean isPage(HttpServletRequest request, String page) {
        String appUrl = String.valueOf(request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE));
        return (StringUtils.isNotBlank(appUrl) && appUrl.startsWith(page));
    }

    @SuppressWarnings("all")
    public String getIdPathVariable(HttpServletRequest request) {
        Map<String, Object> pathVariables = (Map<String, Object>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        return String.valueOf(pathVariables.get("id"));
    }

    public int getIdValue(HttpServletRequest request) {
        String idPathVariable = getIdPathVariable(request);
        return StringUtils.isNotBlank(idPathVariable) ? Integer.parseInt(idPathVariable) : 0;
    }

    public boolean isShowCreate(HttpServletRequest request) {
        return isShowPage(request, Constants.APP_ACTION_CREATE);
    }

    public boolean isShowEdit(HttpServletRequest request) {
        return isShowPage(request, Constants.APP_ACTION_EDIT);
    }

    public boolean isShowValide(HttpServletRequest request) {
        return isShowPage(request, Constants.APP_ACTION_AJAX);
    }

    public boolean isShowView(HttpServletRequest request) {
        return isShowPage(request, Constants.APP_ACTION_VIEW);
    }


    protected boolean isDeleteAction(HttpServletRequest request) {
        return isShowPage(request, Constants.APP_ACTION_DELETE);
    }

    protected boolean isShowIndex(HttpServletRequest request) {
        return isShowPage(request, Constants.APP_ACTION_LIST);
    }

    public boolean hasAccess(Model model, List<String> userMenuActions, boolean isTransmissible) {
        Map<String, Boolean> map = allMenuAction.stream().collect(Collectors.toMap(action -> "CAN_" + action, userMenuActions::contains));

        if (isTransmissible) {
            boolean canValidate = map.get("CAN_VALIDATE") || map.get("CAN_REJECT") || map.get("CAN_RETURN") || map.get("CAN_DIFFER");
            boolean canTransmit = map.get("CAN_TRANSMIT");
            boolean canValidateOrTransmit = canValidate || canTransmit;
            boolean canValidateAndTransmit = canValidate && canTransmit;

            map.putAll(Map.of("CAN_TRANSMIT_OR_VALIDATE", canValidateOrTransmit, "CAN_TRANSMIT_AND_VALIDATE", canValidateAndTransmit));
        }

        model.addAllAttributes(map);
        model.addAllAttributes(map2);

        return (getUserPrincipal().isDefaultUser() || map.values().contains(true));
    }

    public UserPrincipal getUserPrincipal() {
        return (UserPrincipal) httpSession.getAttribute(Constants.APP_CREDENTIALS);
    }

    public void setUserPrincipalSession(UserPrincipal userConnected) {
        httpSession.setAttribute(Constants.APP_CREDENTIALS, userConnected);
    }

    public boolean isConnected() {
        return getUserPrincipal().isLoggedIn();
    }

    public int getModifierParValue() {
        if (getAssignationCourant() == null && getUserPrincipal().isDefaultUser())
            return 0;
        else
            return getAssignationCourant().getId();
    }

    public Assignation getAssignationCourant() {
        if (getUserPrincipal() == null || getUserPrincipal().getAssignationCourant() == null)
            return null;
        return getUserPrincipal().getAssignationCourant();
    }

    public Role getRoleCourant() {
        if (getUserPrincipal() != null && getUserPrincipal().getAssignationCourant() != null)
            return getUserPrincipal().getAssignationCourant().getRole();
        else
            return null;
    }

    public Exercice getExercice() {
        return getUserPrincipal().getExercice();
    }

    public List<String> getUserMenuActions(String viewBase, String currAction) {
        List<String> finalList = null;
        if (getUserPrincipal().isDefaultUser())
            return allMenuAction;
        else {
            var listAction = getUserPrincipal().getMenuKeyToActions().get(getCurrentPage(viewBase));

            Predicate<String> predicate = null;

            switch (currAction) {
                case Constants.APP_ACTION_LIST:
                    predicate = (x -> x.equalsIgnoreCase(Constants.APP_ACTION_DELETE) || x.equalsIgnoreCase(Constants.APP_ACTION_VIEW.toUpperCase())
                            || x.equalsIgnoreCase(Constants.APP_ACTION_EDIT.toUpperCase()) || x.equalsIgnoreCase(Constants.APP_ACTION_CREATE.toUpperCase())
                            || x.equalsIgnoreCase(Constants.APP_ACTION_PRINT.toUpperCase())
                            || x.equalsIgnoreCase(Constants.APP_ACTION_PRINT_GLOBAL.toUpperCase()));
                    break;
                case Constants.APP_ACTION_CREATE:
                    predicate = (x -> x.equalsIgnoreCase(Constants.APP_ACTION_CREATE));
                    break;
                case Constants.APP_ACTION_EDIT:
                    predicate = (x -> x.equalsIgnoreCase(Constants.APP_ACTION_EDIT));
                    break;
                case Constants.APP_ACTION_VIEW:
                    predicate = (x -> x.equalsIgnoreCase(Constants.APP_ACTION_VIEW) || x.equalsIgnoreCase(Constants.APP_ACTION_EDIT.toUpperCase())
                            || x.equalsIgnoreCase(Constants.APP_ACTION_DELETE.toUpperCase()) || x.equalsIgnoreCase(Constants.APP_ACTION_PRINT.toUpperCase()));
                    break;
                default:
                    break;
            }
            if (predicate != null)
                finalList = listAction.stream().filter(predicate).collect(Collectors.toList());
            return (finalList == null) ? List.of() : finalList;
        }
    }

    public PageData getPages() {
        return this.pages;
    }


    public String getContext() {
        return this.appContext;
    }

}*/
