package com.app.controller.referentiel;


import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.app.controller.common.BaseController;
import com.app.controller.common.SetupPage;
import com.app.entities.referentiel.Exercice;
import com.app.service.base.BaseService;
import com.app.service.referentiel.ExerciceService;

@Controller
@RequestMapping(Routes.ROUTE_EXERCICE)
public class ExerciceController extends BaseController<Exercice> {

    private final ExerciceService service;

    public ExerciceController(ExerciceService service, SetupPage setup) {
        super(setup);
        this.service = service;
    }

    @Override
    protected void afterCreate(Model model, Exercice entity) {
        boolean isFirst = service.count() <= 0;
        if (isFirst)
            model.addAttribute("newExo", LocalDate.now().getYear());
        model.addAttribute("isFirstExercice", isFirst);
    }

    @Override
    protected void afterEdit(Model model, Exercice entity) {
        boolean isFirst = service.count() <= 1;
        model.addAttribute("isFirstExercice", isFirst);
    }

    @Override
    protected Map<String, String> getMapDefaultValue() {
        var options = new HashMap<>(Map.of("featureTitle", "label.feature.exercice", "pageTitle.new",
                "label.title.exercice.add",
                "pageTitle.edit", "label.title.exercice.edit", "pageListTitle",
                "label.title.exercice.list"));
        options.put("pageTitle.view", "label.title.exercice.view");

        return options;
    }

    

    @Override
    protected BaseService<Exercice> getService() {
        return service;
    }

    @Override
    protected String getFeatureLink() {
        return Routes.ROUTE_EXERCICE;
    }
}
