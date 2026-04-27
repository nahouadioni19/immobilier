package com.app.controller.common;

import static com.app.utils.Constants.MODEL_ATTRIBUTE_AJAX_RESPONSE;
import static com.app.utils.Constants.MODEL_ATTRIBUTE_AJAX_SUBMIT;
import static com.app.utils.Constants.MODEL_ATTRIBUTE_ENTITIES;
import static com.app.utils.Constants.MODEL_ATTRIBUTE_ENTITY;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
//import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.app.entities.BaseEntity;
import com.app.entities.administration.Assignation;
import com.app.entities.administration.Ministere;
import com.app.entities.administration.Role;
import com.app.entities.referentiel.Exercice;
import com.app.security.UserPrincipal;
import com.app.service.base.BaseService;
import com.app.session.PageData;
import com.app.utils.Constants;
import com.app.utils.JUtils;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public abstract class BaseController<E> {

    @Value("${server.servlet.context-path}")
    protected String appContext;

    private final SetupPage setup;

    @RequestMapping(value = {Routes.ROUTE_LIST, Routes.ROUTE_DELETE}, method = {RequestMethod.GET, RequestMethod.POST})
    public String showIndexOrDeleteAction(Model model, HttpServletRequest request, @RequestParam Map<String, String> params) {


        String currentAction = getCurrentAction(request);

        // forcing ...
        if (getUtilisateurCourant().getUtilisateur().isResetPwd())
            return Routes.ROUTE_REDIRECT_FIRST_CONNEXION_URL;

        if (!setup.hasAccess(model, getUserMenuActions(request, currentAction), isTransmissible()))
            return Routes.REDIRECT_ACCESS_DENIED_URL;

        switch (currentAction) {
            case Constants.APP_ACTION_LIST:
                beforeIndex(model, params);
                index(model);
                setup.listPage(model, getOptions(request), false);
                break;

            case Constants.APP_ACTION_DELETE:
                delete(model, setup.getIdValue(request));
                setup.listPage(model, getOptions(request), true);
                break;
            default:
                break;
        }

        boolean ajaxCase = "true".equalsIgnoreCase(params.get("ajax"));

        if (ajaxCase) {
            return ajaxCaseResponse(model, request, currentAction, "");
        }

        return (getViewBase(request) + Constants.SEPARATEUR_URL + Pages.PAGE_LIST);

    }

    private String getCurrentAction(HttpServletRequest request) {
        if (setup.isShowCreate(request))
            return Constants.APP_ACTION_CREATE;
        else if (setup.isShowEdit(request))
            return Constants.APP_ACTION_EDIT;
        else if (setup.isShowView(request))
            return Constants.APP_ACTION_VIEW;
        else if (setup.isShowIndex(request))
            return Constants.APP_ACTION_LIST;
        else if (setup.isDeleteAction(request)){
            return Constants.APP_ACTION_DELETE;
        }
        return "";
    }

    protected void beforeIndex(Model model, Map<String, String> params) {}

    @GetMapping({Routes.ROUTE_CREATE, Routes.ROUTE_EDIT, Routes.ROUTE_VIEW})
    public String showCreateOrEdit(@ModelAttribute(MODEL_ATTRIBUTE_ENTITIES) E list, BindingResult result, Model model, HttpServletRequest request,
            @RequestParam Map<String, String> params) {
        String currentAction = getCurrentAction(request);

        // forcing ...
        if (getUtilisateurCourant().getUtilisateur().isResetPwd())
            return Routes.ROUTE_REDIRECT_FIRST_CONNEXION_URL;

        if (!setup.hasAccess(model, getUserMenuActions(request, currentAction), isTransmissible()))
            return Routes.REDIRECT_ACCESS_DENIED_URL;

        E entity = getNewInstance();
        boolean isViewPage = false;
        initForm(model);
        switch (currentAction) {
            case Constants.APP_ACTION_CREATE:
                beforeShowCreate(model, entity);
                create(model);
                afterCreate(model, entity);
                setup.createPage(model, getOptions(request), false);
                break;
            case Constants.APP_ACTION_EDIT:
                entity = getEntityFromId(model, request);
                edit(model, entity);
                afterEdit(model, entity);
                setup.editPage(model, getOptions(request), false);
                break;
            case Constants.APP_ACTION_VIEW:
                entity = getEntityFromId(model, request);
                view(model, entity);
                afterView(model, entity);
                setup.viewPage(model, getOptions(request));
                isViewPage = true;
                break;

            default:
                break;
        }
        model.addAttribute("entity", entity);
        return (getViewBase(request) + Constants.SEPARATEUR_URL + (isViewPage ? Pages.PAGE_VIEW : Pages.PAGE_FORM));

    }

    @PostMapping({Routes.ROUTE_CREATE, Routes.ROUTE_EDIT})
    public String saveOrUpdateClass(@Validated @ModelAttribute(MODEL_ATTRIBUTE_ENTITY) E entity, BindingResult result, Model model, HttpServletRequest request,
            @RequestParam Map<String, String> params) {

        String currentAction = getCurrentAction(request);

        if (!setup.hasAccess(model, getUserMenuActions(request, currentAction), isTransmissible()))
            return Routes.REDIRECT_ACCESS_DENIED_URL;

        prepareEntity(model, entity);

        switch (currentAction) {
            case Constants.APP_ACTION_CREATE:
                save(model, entity);
                setup.createPage(model, getOptions(request), true);
                break;
            case Constants.APP_ACTION_EDIT:
                update(model, entity);
                setup.editPage(model, getOptions(request), true);
                break;

            default:
                break;
        }


        boolean ajaxCase = "true".equalsIgnoreCase(params.get("ajax"));

        if (ajaxCase) {
            return ajaxCaseResponse(model, request, currentAction, "");
        }

        return (getViewBase(request) + Constants.SEPARATEUR_URL + Pages.PAGE_FORM);
    }

    @RequestMapping(value = Routes.ROUTE_AJAX, method = {RequestMethod.GET, RequestMethod.POST})
    public String showChangeAjax(Model model, HttpServletRequest request, @RequestParam Map<String, String> params) {
        ajaxAction(model, request, params);
        afterAjax(model, request, params);
        return getViewBase(request) + Constants.SEPARATEUR_URL + Pages.PAGE_AJAX;
    }

    protected String getCategorie() {
        return null;
    }

    protected String getStatutCourant(String step) {
        return null;
    }

    protected Map<String, String> getMapDefaultValue() {
        var options = new HashMap<>(Map.of("featureTitle", "title.feature", "pageTitle.new", "title.page.title.new", "pageTitle.edit", "title.page.title.edit",
                "pageListTitle", "title.page.title.list"));
        options.put("pageTitle.view", "title.page.title.view");
        return options;
    }

    protected Map<String, String> getOptions(HttpServletRequest request) {
        Map<String, String> options = getMapDefaultValue();
        options.put("viewPath", StringUtils.defaultIfBlank(appContext, "") + getViewBase(request));
        options.put("featureLink", getViewBase(request));
        options.put("isTransmissible", String.valueOf(isTransmissible()));
        options.put("tabEnAttente", "label.tab.en.attente");
        options.put("tabValide", "label.tab.valide");
        options.put("tabTransmis", "label.tab.transmis");
        options.put("isViewObs", String.valueOf(isViewObservateur()));

        return addOnsOptions(request, options);
    }

    protected String ajaxCaseResponse(Model model, HttpServletRequest request, String action, String resultMessage) {
        ajaxPartResponse(model, request, action, resultMessage);

        return getViewBase(request) + Constants.SEPARATEUR_URL + Pages.PAGE_AJAX;
    }

    protected void ajaxPartResponse(Model model, HttpServletRequest request, String action, String resultMessage) {
        Map<String, String> response = new HashMap<>();
        if ("error".equals(action))
            response.put("hasError", "true");
        response.put(Constants.AJAX_RESPONSE_RESULT, action);
        response.put(Constants.AJAX_RESPONSE_MESSAGE, resultMessage);
        response.put(Constants.AJAX_RESPONSE_TARGET_PAGE, StringUtils.defaultIfBlank(appContext, "") + getViewBase(request) + Routes.ROUTE_LIST);
        model.addAttribute(MODEL_ATTRIBUTE_AJAX_SUBMIT, true);
        model.addAttribute(MODEL_ATTRIBUTE_AJAX_RESPONSE, JUtils.toJSON(response));
    }

    protected Map<String, String> addOnsOptions(HttpServletRequest request, Map<String, String> option) {
        return option;
    }

    protected void initForm(Model model) {}

    protected void beforeShowCreate(Model model, E entity) {}

    protected void create(Model model) {}

    protected void afterCreate(Model model, E entity) {}

    protected void afterView(Model model, E entity) {
        afterEdit(model, entity);
    }

    protected void edit(Model model, E entity) {}

    protected void afterEdit(Model model, E entity) {}

    protected void view(Model model, E entity) {
        edit(model, entity);
    }

    protected void prepareEntity(Model model, E entity) {
        setActionUser((BaseEntity) entity);
    }

    protected void save(Model model, E entity) {
        getService().save(entity);
    }

    protected void update(Model model, E entity) {
        getService().update(entity);
    }

    protected void delete(Model model, int entityId) {
        getService().delete(entityId);
    }

    protected void ajaxAction(Model model, HttpServletRequest request, Map<String, String> params) {}

    protected void afterAjax(Model model, HttpServletRequest request, Map<String, String> params) {}

    protected void index(Model model) {
        if (isTransmissible()) {
            indexTransmissible(model);
        } else {
            List<E> entities = getService().findAll();
            model.addAttribute(MODEL_ATTRIBUTE_ENTITIES, entities);
        }
        afterIndex(model);
    }

    protected void indexTransmissible(Model model) {
        model.addAttribute("list_tab1", List.of());
        model.addAttribute("list_tab2", List.of());
        model.addAttribute("list_tab3", List.of());
        model.addAttribute("LAST_STEP", isLastStep());
    }

    protected void afterIndex(Model model) {}

    protected abstract String getFeatureLink();

    protected String getViewBase(HttpServletRequest request) {
        return getFeatureLink();
    }

    private E getEntityFromId(Model model, HttpServletRequest request) {
        E entity = null;
        int entityId = setup.getIdValue(request);
        if (entityId > 0)
            entity = get(entityId);
        return entity;
    }

    private E getNewInstance() {
        E entity = null;
        try {
            entity = getEntityClass().getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
            e.printStackTrace();
        }
        return entity;
    }

    @SuppressWarnings("unchecked")
    private Class<E> getEntityClass() {
        return (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public E get(int id) {
        return getService().findById(id).orElseThrow(() -> new IllegalArgumentException(getEntityClass().getSimpleName() + " avec Id:" + id + " introuvable."));
    }

    protected BaseService<E> getService() {
        return null;
    }

    private <T extends BaseEntity> void setActionUser(T entity) {
        entity.setModifiePar(getModifierParValue());
    }

    protected UserPrincipal getUtilisateurCourant() {
        return setup.getUserPrincipal();
    }

    private int getModifierParValue() {
        return setup.getModifierParValue();
    }

    protected Assignation getAssignationCourant() {
        return setup.getAssignationCourant();
    }

    protected Role getRoleCourant() {
        return setup.getRoleCourant();
    }

    public Exercice getExercice() {
        return setup.getExercice();
    }

    protected Ministere getMinistereCourant() {
        return (getRoleCourant() != null) ? getRoleCourant().getMinistere() : null;
    }

    protected List<String> getUserMenuActions(HttpServletRequest request, String currAction) {
        return setup.getUserMenuActions(getViewBase(request), currAction);
    }

    protected boolean isTransmissible() {
        return false;
    }

    protected boolean isViewObservateur() {
        return false;
    }

    protected boolean isLastStep() {
        return getUtilisateurCourant().isDefaultUser();
    }

    protected SetupPage getSetup() {
        return setup;
    }

    protected PageData getPages() {
        return this.getSetup().getPages();
    }

    protected String getActionVal(Map<String, String> params) {
        var action = JUtils.getMapValue(params, Constants.ACTION);
        return StringUtils.isNotBlank(action) ? action : "";
    }

}