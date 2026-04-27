package com.app.controller.referentiel;

import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;

import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfWriter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.app.dto.DashboardLoyerDTO;
import com.app.dto.DashboardLoyerMontantDTO;
import com.app.service.recouvre.BailService;
import com.app.service.recouvre.DashboardService;
import com.app.utils.PdfFooter;
import com.lowagie.text.Font;
import com.lowagie.text.Element;

//import com.lowagie.text.BaseColor;

import java.awt.Color;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/dashboards")
public class DashboardController {

    private final BailService bailService;
    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(BailService bailService,
                               DashboardService dashboardService) {
        this.bailService = bailService;
        this.dashboardService = dashboardService;
    }

    /* =========================
       📊 DASHBOARD GLOBAL
       ========================= */
    @GetMapping
    public String dashboardGlobal(Model model) {

        model.addAttribute("bailsActifs", bailService.countBailsActifs());
        model.addAttribute("bailsResilies", bailService.countBailsResilies());

        // Exemple graphique
        List<String> mois = List.of("Janvier", "Février", "Mars");
        List<Integer> loyers = List.of(120000, 135000, 128000);

        model.addAttribute("mois", mois);
        model.addAttribute("loyers", loyers);

        return "dashboard/form";
    }

    /* =========================
       📅 DASHBOARD LOYERS
       ========================= */
    @GetMapping("/loyers")
    public String dashboardLoyers(Model model,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(required = false) Integer annee) {

        if (annee == null) {
            annee = Year.now().getValue();
        }

        Page<DashboardLoyerDTO> dashboardPage =
                dashboardService.getDashboard(annee, PageRequest.of(page, 10));

        model.addAttribute("dashboardPage", dashboardPage);
        model.addAttribute("dashboard", dashboardPage.getContent());

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", dashboardPage.getTotalPages());

        model.addAttribute("annee", annee);

        return "dashboard/loyers";
    }
    
    private String formatMontant(Long montant) {
        if (montant == null) return "0";
        return String.format("%,d", montant).replace(',', ' ');
    }
    
 // 💰 FCFA
  /*  @GetMapping("/loyers-montant")
    public String dashboardLoyersMontant(Model model,
                                         @RequestParam(required = false) Integer annee,
                                         @RequestParam(required = false) String search,
                                         @RequestParam(defaultValue = "0") int page) {

        if (annee == null) {
            annee = Year.now().getValue();
        }

        Page<DashboardLoyerMontantDTO> pageData =
                dashboardService.getDashboardMontant(annee, search, PageRequest.of(page, 10));

        List<DashboardLoyerMontantDTO> dashboard = pageData.getContent();

        // ================= TOTALS =================
        model.addAttribute("totalJan", dashboard.stream().mapToLong(d -> d.getJan() == null ? 0 : d.getJan()).sum());
        model.addAttribute("totalFev", dashboard.stream().mapToLong(d -> d.getFev() == null ? 0 : d.getFev()).sum());
        model.addAttribute("totalMar", dashboard.stream().mapToLong(d -> d.getMar() == null ? 0 : d.getMar()).sum());
        model.addAttribute("totalAvr", dashboard.stream().mapToLong(d -> d.getAvr() == null ? 0 : d.getAvr()).sum());
        model.addAttribute("totalMai", dashboard.stream().mapToLong(d -> d.getMai() == null ? 0 : d.getMai()).sum());
        model.addAttribute("totalJui", dashboard.stream().mapToLong(d -> d.getJui() == null ? 0 : d.getJui()).sum());
        model.addAttribute("totalJul", dashboard.stream().mapToLong(d -> d.getJul() == null ? 0 : d.getJul()).sum());
        model.addAttribute("totalAou", dashboard.stream().mapToLong(d -> d.getAou() == null ? 0 : d.getAou()).sum());
        model.addAttribute("totalSep", dashboard.stream().mapToLong(d -> d.getSep() == null ? 0 : d.getSep()).sum());
        model.addAttribute("totalOct", dashboard.stream().mapToLong(d -> d.getOct() == null ? 0 : d.getOct()).sum());
        model.addAttribute("totalNov", dashboard.stream().mapToLong(d -> d.getNov() == null ? 0 : d.getNov()).sum());
        model.addAttribute("totalDec", dashboard.stream().mapToLong(d -> d.getDec() == null ? 0 : d.getDec()).sum());

     // ================= CALCUL DES TOTAUX =================
        long totalJan = dashboard.stream().mapToLong(d -> d.getJan() == null ? 0 : d.getJan()).sum();
        long totalFev = dashboard.stream().mapToLong(d -> d.getFev() == null ? 0 : d.getFev()).sum();
        long totalMar = dashboard.stream().mapToLong(d -> d.getMar() == null ? 0 : d.getMar()).sum();
        long totalAvr = dashboard.stream().mapToLong(d -> d.getAvr() == null ? 0 : d.getAvr()).sum();
        long totalMai = dashboard.stream().mapToLong(d -> d.getMai() == null ? 0 : d.getMai()).sum();
        long totalJui = dashboard.stream().mapToLong(d -> d.getJui() == null ? 0 : d.getJui()).sum();
        long totalJul = dashboard.stream().mapToLong(d -> d.getJul() == null ? 0 : d.getJul()).sum();
        long totalAou = dashboard.stream().mapToLong(d -> d.getAou() == null ? 0 : d.getAou()).sum();
        long totalSep = dashboard.stream().mapToLong(d -> d.getSep() == null ? 0 : d.getSep()).sum();
        long totalOct = dashboard.stream().mapToLong(d -> d.getOct() == null ? 0 : d.getOct()).sum();
        long totalNov = dashboard.stream().mapToLong(d -> d.getNov() == null ? 0 : d.getNov()).sum();
        long totalDec = dashboard.stream().mapToLong(d -> d.getDec() == null ? 0 : d.getDec()).sum();
        
        long totalGlobal = dashboard.stream()
        	    .mapToLong(DashboardLoyerMontantDTO::getTotal)
        	    .sum();

       // model.addAttribute("totalGlobal", totalGlobal);
        model.addAttribute("totalJanFormatted", formatMontant(totalJan));
        model.addAttribute("totalFevFormatted", formatMontant(totalFev));        
        model.addAttribute("totalMarFormatted", formatMontant(totalMar));
        model.addAttribute("totalAvrFormatted", formatMontant(totalAvr)); 
        model.addAttribute("totalMaiFormatted", formatMontant(totalMai));
        model.addAttribute("totalJuiFormatted", formatMontant(totalJui));
        model.addAttribute("totalJulFormatted", formatMontant(totalJul));
        model.addAttribute("totalAouFormatted", formatMontant(totalAou));
        model.addAttribute("totalSepFormatted", formatMontant(totalSep));        
        model.addAttribute("totalOctFormatted", formatMontant(totalOct));
        model.addAttribute("totalNovFormatted", formatMontant(totalNov));        
        model.addAttribute("totalDecFormatted", formatMontant(totalDec));
        
        model.addAttribute("totalGlobalFormatted", formatMontant(totalGlobal));
        // ================= MODEL =================
        model.addAttribute("dashboard", dashboard);
        model.addAttribute("annee", annee);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageData.getTotalPages());
        model.addAttribute("search", search);
        
        return "dashboard/loyers-montant";
    }*/
    
 // 💰 FCFA
    @GetMapping("/loyers-montant")
    public String dashboardLoyersMontant(Model model,
                                         @RequestParam(required = false) Integer annee,
                                         @RequestParam(required = false) String search,
                                         @RequestParam(defaultValue = "0") int page) {

        if (annee == null) {
            annee = Year.now().getValue();
        }

        Page<DashboardLoyerMontantDTO> pageData =
                dashboardService.getDashboardMontant(annee, search, PageRequest.of(page, 10));

        List<DashboardLoyerMontantDTO> dashboard = pageData.getContent();

        // ================= CALCUL DES TOTAUX =================
        long totalJan = dashboard.stream().mapToLong(d -> safe(d.getJan())).sum();
        long totalFev = dashboard.stream().mapToLong(d -> safe(d.getFev())).sum();
        long totalMar = dashboard.stream().mapToLong(d -> safe(d.getMar())).sum();
        long totalAvr = dashboard.stream().mapToLong(d -> safe(d.getAvr())).sum();
        long totalMai = dashboard.stream().mapToLong(d -> safe(d.getMai())).sum();
        long totalJui = dashboard.stream().mapToLong(d -> safe(d.getJui())).sum();
        long totalJul = dashboard.stream().mapToLong(d -> safe(d.getJul())).sum();
        long totalAou = dashboard.stream().mapToLong(d -> safe(d.getAou())).sum();
        long totalSep = dashboard.stream().mapToLong(d -> safe(d.getSep())).sum();
        long totalOct = dashboard.stream().mapToLong(d -> safe(d.getOct())).sum();
        long totalNov = dashboard.stream().mapToLong(d -> safe(d.getNov())).sum();
        long totalDec = dashboard.stream().mapToLong(d -> safe(d.getDec())).sum();

        long totalGlobal = dashboard.stream()
                .mapToLong(d -> safe(d.getTotal()))
                .sum();

        // ================= FORMAT =================
        model.addAttribute("totalJanFormatted", formatMontant(totalJan));
        model.addAttribute("totalFevFormatted", formatMontant(totalFev));
        model.addAttribute("totalMarFormatted", formatMontant(totalMar));
        model.addAttribute("totalAvrFormatted", formatMontant(totalAvr));
        model.addAttribute("totalMaiFormatted", formatMontant(totalMai));
        model.addAttribute("totalJuiFormatted", formatMontant(totalJui));
        model.addAttribute("totalJulFormatted", formatMontant(totalJul));
        model.addAttribute("totalAouFormatted", formatMontant(totalAou));
        model.addAttribute("totalSepFormatted", formatMontant(totalSep));
        model.addAttribute("totalOctFormatted", formatMontant(totalOct));
        model.addAttribute("totalNovFormatted", formatMontant(totalNov));
        model.addAttribute("totalDecFormatted", formatMontant(totalDec));
        model.addAttribute("totalGlobalFormatted", formatMontant(totalGlobal));

        // ================= MODEL =================
        model.addAttribute("dashboard", dashboard);
        model.addAttribute("annee", annee);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageData.getTotalPages());
        model.addAttribute("search", search);

        return "dashboard/loyers-montant";
    }
    
    // ✅ ICI
    private long safe(Long value) {
        return value == null ? 0L : value;
    }
        
   /* @GetMapping(value ="/loyers-montant/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> searchDashboard(
            @RequestParam int annee,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page) {

        Page<DashboardLoyerMontantDTO> pageData =
                dashboardService.getDashboardMontant(
                        annee,
                        search,
                        PageRequest.of(page, 10)
                );

        Map<String, Object> response = new HashMap<>();
        response.put("content", pageData.getContent());
        response.put("totalPages", pageData.getTotalPages());
        response.put("page", pageData.getNumber());

        return response;
    }*/
    
    @GetMapping(value ="/loyers-montant/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> searchDashboard(
            @RequestParam int annee,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page) {

        Page<DashboardLoyerMontantDTO> pageData =
                dashboardService.getDashboardMontant(
                        annee,
                        search,
                        PageRequest.of(page, 10)
                );

        // 🔥 AJOUT IMPORTANT : totaux dynamiques
        Map<String, Long> totals = dashboardService.getTotals(annee, search);

        Map<String, Object> response = new HashMap<>();
        response.put("content", pageData.getContent());
        response.put("totalPages", pageData.getTotalPages());
        response.put("page", pageData.getNumber());

        // 🔥 AJOUT
        response.put("totals", totals);

        return response;
    }
    
    @GetMapping("/loyers-montant/pdf")
    public void exportPdf(
            @RequestParam int annee,
            @RequestParam(required = false) String search,
            HttpServletResponse response) throws Exception {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=loyers.pdf");

        List<DashboardLoyerMontantDTO> data = dashboardService.getDashboard(annee, search);
      //  Map<String, Long> totals = dashboardService.getTotals(annee, search);
        Map<String, Long> totals = calculateTotals(data);

        // 📄 PDF paysage
        Document document = new Document(PageSize.A4.rotate(), 20, 20, 20, 20);

        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
        writer.setPageEvent(new PdfFooter()); // ✅ pagination activée

        document.open();

        // 📌 TITRE
        Font titleFont = new Font(Font.HELVETICA, 14, Font.BOLD);
        Paragraph title = new Paragraph("TABLEAU DES LOYERS - " + annee, titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10);
        document.add(title);

        // 📊 TABLE
        PdfPTable table = new PdfPTable(14);
        table.setWidthPercentage(100);
        table.setHeaderRows(1); // ✅ répète le header sur chaque page

        table.setWidths(new float[]{
                3f,
                1f,1f,1f,1f,1f,1f,
                1f,1f,1f,1f,1f,1f,
                1.5f
        });

        // 📌 HEADER
        Font headerFont = new Font(Font.HELVETICA, 9, Font.BOLD);

        String[] headers = {
                "Locataire","Jan","Fev","Mar","Avr","Mai","Juin",
                "Juil","Aout","Sep","Oct","Nov","Dec","Total"
        };

        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }

        // 📌 TBODY
        Font normalFont = new Font(Font.HELVETICA, 9);

        for (DashboardLoyerMontantDTO row : data) {

            table.addCell(createCell(row.getLocataireCourt(), normalFont, Element.ALIGN_LEFT));

            table.addCell(createCell(row.getJanFormatted(), normalFont, Element.ALIGN_RIGHT));
            table.addCell(createCell(row.getFevFormatted(), normalFont, Element.ALIGN_RIGHT));
            table.addCell(createCell(row.getMarFormatted(), normalFont, Element.ALIGN_RIGHT));
            table.addCell(createCell(row.getAvrFormatted(), normalFont, Element.ALIGN_RIGHT));
            table.addCell(createCell(row.getMaiFormatted(), normalFont, Element.ALIGN_RIGHT));
            table.addCell(createCell(row.getJuiFormatted(), normalFont, Element.ALIGN_RIGHT));
            table.addCell(createCell(row.getJulFormatted(), normalFont, Element.ALIGN_RIGHT));
            table.addCell(createCell(row.getAouFormatted(), normalFont, Element.ALIGN_RIGHT));
            table.addCell(createCell(row.getSepFormatted(), normalFont, Element.ALIGN_RIGHT));
            table.addCell(createCell(row.getOctFormatted(), normalFont, Element.ALIGN_RIGHT));
            table.addCell(createCell(row.getNovFormatted(), normalFont, Element.ALIGN_RIGHT));
            table.addCell(createCell(row.getDecFormatted(), normalFont, Element.ALIGN_RIGHT));

            table.addCell(createCell(row.getTotalFormatted(), normalFont, Element.ALIGN_RIGHT));
        }

        // 📌 TFOOT
        Font totalFont = new Font(Font.HELVETICA, 9, Font.BOLD);

        table.addCell(createCell("TOTAL", totalFont, Element.ALIGN_CENTER));

        table.addCell(createCell(format(totals.get("m1")), totalFont, Element.ALIGN_RIGHT));
        table.addCell(createCell(format(totals.get("m2")), totalFont, Element.ALIGN_RIGHT));
        table.addCell(createCell(format(totals.get("m3")), totalFont, Element.ALIGN_RIGHT));
        table.addCell(createCell(format(totals.get("m4")), totalFont, Element.ALIGN_RIGHT));
        table.addCell(createCell(format(totals.get("m5")), totalFont, Element.ALIGN_RIGHT));
        table.addCell(createCell(format(totals.get("m6")), totalFont, Element.ALIGN_RIGHT));
        table.addCell(createCell(format(totals.get("m7")), totalFont, Element.ALIGN_RIGHT));
        table.addCell(createCell(format(totals.get("m8")), totalFont, Element.ALIGN_RIGHT));
        table.addCell(createCell(format(totals.get("m9")), totalFont, Element.ALIGN_RIGHT));
        table.addCell(createCell(format(totals.get("m10")), totalFont, Element.ALIGN_RIGHT));
        table.addCell(createCell(format(totals.get("m11")), totalFont, Element.ALIGN_RIGHT));
        table.addCell(createCell(format(totals.get("m12")), totalFont, Element.ALIGN_RIGHT));
        table.addCell(createCell(format(totals.get("total")), totalFont, Element.ALIGN_RIGHT));

        document.add(table);

        // ❌ PAS BESOIN DE newPage() ici
        document.close();
    }

    private PdfPCell createCell(String value, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(value, font));
        cell.setHorizontalAlignment(align);
        cell.setPadding(4);
        return cell;
    }

    private String format(Long val) {
        if (val == null || val == 0) return "0";
        return String.format("%,d", val).replace(',', ' ');
    }    
    
    private Map<String, Long> calculateTotals(List<DashboardLoyerMontantDTO> data) {

        Map<String, Long> totals = new HashMap<>();

        long m1=0,m2=0,m3=0,m4=0,m5=0,m6=0,
             m7=0,m8=0,m9=0,m10=0,m11=0,m12=0,total=0;

        for (DashboardLoyerMontantDTO row : data) {
            m1 += safe(row.getJan());
            m2 += safe(row.getFev());
            m3 += safe(row.getMar());
            m4 += safe(row.getAvr());
            m5 += safe(row.getMai());
            m6 += safe(row.getJui());
            m7 += safe(row.getJul());
            m8 += safe(row.getAou());
            m9 += safe(row.getSep());
            m10 += safe(row.getOct());
            m11 += safe(row.getNov());
            m12 += safe(row.getDec());

            total += row.getTotal();
        }

        totals.put("m1", m1);
        totals.put("m2", m2);
        totals.put("m3", m3);
        totals.put("m4", m4);
        totals.put("m5", m5);
        totals.put("m6", m6);
        totals.put("m7", m7);
        totals.put("m8", m8);
        totals.put("m9", m9);
        totals.put("m10", m10);
        totals.put("m11", m11);
        totals.put("m12", m12);
        totals.put("total", total);

        return totals;
    }

    
        
   /* @GetMapping("/search")
    public Map<String, Object> search(
            @RequestParam int annee,
            @RequestParam(required = false) String search,
            @RequestParam int page
    ) {

        Pageable pageable = PageRequest.of(page, 10);

        Page<DashboardLoyerMontantDTO> result =
        		dashboardService.getDashboardMontant(annee, search, pageable);

        Map<String, Long> totals = dashboardService.getTotals(annee, search);

        return Map.of(
            "content", result.getContent(),
            "number", result.getNumber(),
            "totalPages", result.getTotalPages(),
            "totals", totals
        );
    }*/
    
   /* @GetMapping("/loyers-montant/pdf")
    public void exportPdf(
            @RequestParam int annee,
            @RequestParam(required = false) String search,
            HttpServletResponse response) throws Exception {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=loyers.pdf");

        List<DashboardLoyerMontantDTO> data = dashboardService.getDashboard(annee, search);
        Map<String, Long> totals = dashboardService.getTotals(annee, search);

        Document document = new Document(PageSize.A4.rotate(), 20, 20, 20, 20);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
        Paragraph title = new Paragraph("TABLEAU DES LOYERS - " + annee, titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10);
        document.add(title);

        PdfPTable table = new PdfPTable(14);
        table.setWidthPercentage(100);

        table.setWidths(new float[]{
                3f,
                1f,1f,1f,1f,1f,1f,
                1f,1f,1f,1f,1f,1f,
                1.5f
        });

        Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD);
      //  Color headerColor = new Color(200, 230, 200);
        BaseColor headerColor = new BaseColor(200, 230, 200);

        String[] headers = {
                "Locataire","Jan","Fev","Mar","Avr","Mai","Juin",
                "Juil","Aout","Sep","Oct","Nov","Dec","Total"
        };

        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(headerColor);
            cell.setPadding(5);
            table.addCell(cell);
        }

        boolean alternate = false;

        for (DashboardLoyerMontantDTO row : data) {

          /*  Color rowColor = alternate
                    ? new Color(245, 245, 245)
                    : Color.WHITE;
            BaseColor rowColor = alternate ? new BaseColor(245, 245, 245) : BaseColor.WHITE;
            alternate = !alternate;

            table.addCell(createCell(row.getLocataireCourt(), rowColor, Element.ALIGN_LEFT));

            table.addCell(createCell(row.getJanFormatted(), rowColor, Element.ALIGN_RIGHT));
            table.addCell(createCell(row.getFevFormatted(), rowColor, Element.ALIGN_RIGHT));
            table.addCell(createCell(row.getMarFormatted(), rowColor, Element.ALIGN_RIGHT));
            table.addCell(createCell(row.getAvrFormatted(), rowColor, Element.ALIGN_RIGHT));
            table.addCell(createCell(row.getMaiFormatted(), rowColor, Element.ALIGN_RIGHT));
            table.addCell(createCell(row.getJuiFormatted(), rowColor, Element.ALIGN_RIGHT));
            table.addCell(createCell(row.getJulFormatted(), rowColor, Element.ALIGN_RIGHT));
            table.addCell(createCell(row.getAouFormatted(), rowColor, Element.ALIGN_RIGHT));
            table.addCell(createCell(row.getSepFormatted(), rowColor, Element.ALIGN_RIGHT));
            table.addCell(createCell(row.getOctFormatted(), rowColor, Element.ALIGN_RIGHT));
            table.addCell(createCell(row.getNovFormatted(), rowColor, Element.ALIGN_RIGHT));
            table.addCell(createCell(row.getDecFormatted(), rowColor, Element.ALIGN_RIGHT));

            Font boldFont = new Font(Font.HELVETICA, 10, Font.BOLD);
            PdfPCell totalCell = new PdfPCell(new Phrase(row.getTotalFormatted(), boldFont));
            totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalCell.setBackgroundColor(rowColor);
            totalCell.setPadding(4);
            table.addCell(totalCell);
        }

        Font totalFont = new Font(Font.HELVETICA, 10, Font.BOLD);
       // Color totalColor = new Color(220, 220, 220);
        BaseColor totalColor = new BaseColor(220, 220, 220);

        table.addCell(createHeaderCell("TOTAL", totalFont, totalColor));

        table.addCell(createCell(format(totals.get("m1")), totalColor, Element.ALIGN_RIGHT));
        table.addCell(createCell(format(totals.get("m2")), totalColor, Element.ALIGN_RIGHT));
        table.addCell(createCell(format(totals.get("m3")), totalColor, Element.ALIGN_RIGHT));
        table.addCell(createCell(format(totals.get("m4")), totalColor, Element.ALIGN_RIGHT));
        table.addCell(createCell(format(totals.get("m5")), totalColor, Element.ALIGN_RIGHT));
        table.addCell(createCell(format(totals.get("m6")), totalColor, Element.ALIGN_RIGHT));
        table.addCell(createCell(format(totals.get("m7")), totalColor, Element.ALIGN_RIGHT));
        table.addCell(createCell(format(totals.get("m8")), totalColor, Element.ALIGN_RIGHT));
        table.addCell(createCell(format(totals.get("m9")), totalColor, Element.ALIGN_RIGHT));
        table.addCell(createCell(format(totals.get("m10")), totalColor, Element.ALIGN_RIGHT));
        table.addCell(createCell(format(totals.get("m11")), totalColor, Element.ALIGN_RIGHT));
        table.addCell(createCell(format(totals.get("m12")), totalColor, Element.ALIGN_RIGHT));
        table.addCell(createCell(format(totals.get("total")), totalColor, Element.ALIGN_RIGHT));

        document.add(table);
        document.close();
    }
    
    private PdfPCell createCell(String value, java.awt.Color color, int align) {

        BaseColor baseColor = new BaseColor(
                color.getRed(),
                color.getGreen(),
                color.getBlue()
        );

        PdfPCell cell = new PdfPCell(new Phrase(value));
        cell.setHorizontalAlignment(align);
        cell.setBackgroundColor(baseColor);
        cell.setPadding(4);

        return cell;
    }

    private PdfPCell createHeaderCell(String value, Font font, java.awt.Color color) {

        BaseColor baseColor = new BaseColor(
                color.getRed(),
                color.getGreen(),
                color.getBlue()
        );

        PdfPCell cell = new PdfPCell(new Phrase(value, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(baseColor);
        cell.setPadding(5);

        return cell;
    }

    private String format(Long val) {
        if (val == null || val == 0) return "0";
        return String.format("%,d", val).replace(',', ' ');
    }*/
    
}