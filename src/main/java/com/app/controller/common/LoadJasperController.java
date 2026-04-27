/*
 * package com.app.controller.common;
 * 
 * import java.util.HashMap; import java.util.Map;
 * 
 * import jakarta.servlet.http.HttpServletRequest; import
 * jakarta.servlet.http.HttpServletResponse; import
 * lombok.RequiredArgsConstructor; import lombok.extern.slf4j.Slf4j;
 * 
 * import org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.stereotype.Controller; import
 * org.springframework.ui.Model; import
 * org.springframework.web.bind.annotation.ModelAttribute; import
 * org.springframework.web.bind.annotation.PostMapping;
 * 
 * import com.app.entities.rapport.POJOEtat; import
 * com.app.service.common.JasperreportService; import com.app.utils.Constants;
 * import net.sf.jasperreports.engine.JasperPrint;
 * 
 * @Slf4j
 * 
 * @Controller
 * 
 * @RequiredArgsConstructor public class LoadJasperController {
 * 
 * @Autowired private final JasperreportService rptService;
 * 
 * @PostMapping(value = "/report") public String
 * generateReport(@ModelAttribute("Form") POJOEtat form, Model model,
 * HttpServletRequest request, HttpServletResponse response) {
 * 
 * try { JasperPrint jprint = rptService.generateReport(form);
 * 
 * if (jprint != null) { String format = (form.getFormat() == null) ?
 * JasperreportService.PDF : form.getFormat(); String renderType = form.isPdf()
 * ? Constants.SHOW_IN_BROWSER : Constants.DIRECT_DOWNLOAD ; Map<String, String>
 * etatOptions = new HashMap<>(); etatOptions.put(Constants.ETAT_FILE_NAME,
 * form.getInfoJrxml()); etatOptions.put(Constants.ETAT_RENDER_TYPE,
 * renderType); etatOptions.put(Constants.ETAT_FILE_TYPE, format);
 * rptService.doExport(response, jprint, etatOptions); } else
 * log.error("JasperPrint is null"); } catch (Exception e) {
 * e.printStackTrace(); } return null; } }
 * 
 */