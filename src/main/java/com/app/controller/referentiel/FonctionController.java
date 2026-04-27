/*
 * package com.app.controller.referentiel;
 * 
 * 
 * import java.util.HashMap; import java.util.Map;
 * 
 * import org.springframework.stereotype.Controller; import
 * org.springframework.web.bind.annotation.RequestMapping; import
 * com.app.controller.common.BaseController; import
 * com.app.controller.common.SetupPage; import
 * com.app.entities.referentiel.Fonction; import
 * com.app.service.base.BaseService; import
 * com.app.service.referentiel.FonctionService;
 * 
 * @Controller
 * 
 * @RequestMapping(Routes.ROUTE_FONCTION) public class FonctionController
 * extends BaseController<Fonction> {
 * 
 * private final FonctionService service;
 * 
 * public FonctionController(FonctionService service, SetupPage setup) {
 * super(setup); this.service = service; }
 * 
 * @Override protected Map<String, String> getMapDefaultValue() { var options =
 * new HashMap<>(Map.of("featureTitle", "label.feature.fonction",
 * "pageTitle.new", "label.title.fonction.add", "pageTitle.edit",
 * "label.title.fonction.edit", "pageListTitle", "label.title.fonction.list"));
 * options.put("pageTitle.view", "label.title.fonction.view");
 * 
 * return options; }
 * 
 * @Override protected BaseService<Fonction> getService() { return service; }
 * 
 * @Override protected String getFeatureLink() { return Routes.ROUTE_FONCTION; }
 * }
 */