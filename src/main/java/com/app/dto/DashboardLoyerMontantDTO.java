package com.app.dto;

public class DashboardLoyerMontantDTO {

    private String locataire;

    private Long jan;
    private Long fev;
    private Long mar;
    private Long avr;
    private Long mai;
    private Long jui;
    private Long jul;
    private Long aou;
    private Long sep;
    private Long oct;
    private Long nov;
    private Long dec;

    public DashboardLoyerMontantDTO() {}

    public DashboardLoyerMontantDTO(String locataire, Long jan, Long fev, Long mar, Long avr, Long mai, Long jui,
                                    Long jul, Long aou, Long sep, Long oct, Long nov, Long dec) {
        this.locataire = locataire;
        this.jan = jan;
        this.fev = fev;
        this.mar = mar;
        this.avr = avr;
        this.mai = mai;
        this.jui = jui;
        this.jul = jul;
        this.aou = aou;
        this.sep = sep;
        this.oct = oct;
        this.nov = nov;
        this.dec = dec;
    }

    // ----------------------
    // Getters & Setters
    // ----------------------

    public String getLocataire() { return locataire; }
    public void setLocataire(String locataire) { this.locataire = locataire; }
    public Long getJan() { return jan; }
    public void setJan(Long jan) { this.jan = jan; }
    public Long getFev() { return fev; }
    public void setFev(Long fev) { this.fev = fev; }
    public Long getMar() { return mar; }
    public void setMar(Long mar) { this.mar = mar; }
    public Long getAvr() { return avr; }
    public void setAvr(Long avr) { this.avr = avr; }
    public Long getMai() { return mai; }
    public void setMai(Long mai) { this.mai = mai; }
    public Long getJui() { return jui; }
    public void setJui(Long jui) { this.jui = jui; }
    public Long getJul() { return jul; }
    public void setJul(Long jul) { this.jul = jul; }
    public Long getAou() { return aou; }
    public void setAou(Long aou) { this.aou = aou; }
    public Long getSep() { return sep; }
    public void setSep(Long sep) { this.sep = sep; }
    public Long getOct() { return oct; }
    public void setOct(Long oct) { this.oct = oct; }
    public Long getNov() { return nov; }
    public void setNov(Long nov) { this.nov = nov; }
    public Long getDec() { return dec; }
    public void setDec(Long dec) { this.dec = dec; }

    // ----------------------
    // Getters formatés
    // ----------------------

    public String getJanFormatted() { return formatMontant(jan); }
    public String getFevFormatted() { return formatMontant(fev); }
    public String getMarFormatted() { return formatMontant(mar); }
    public String getAvrFormatted() { return formatMontant(avr); }
    public String getMaiFormatted() { return formatMontant(mai); }
    public String getJuiFormatted() { return formatMontant(jui); }
    public String getJulFormatted() { return formatMontant(jul); }
    public String getAouFormatted() { return formatMontant(aou); }
    public String getSepFormatted() { return formatMontant(sep); }
    public String getOctFormatted() { return formatMontant(oct); }
    public String getNovFormatted() { return formatMontant(nov); }
    public String getDecFormatted() { return formatMontant(dec); }

    // Méthode utilitaire pour formater les montants
    private String formatMontant(Long montant) {
        if (montant == null || montant == 0) return "0";
        return String.format("%,d", montant).replace(',', ' ');
    }
    
    public Long getTotal() {
        return safe(jan) + safe(fev) + safe(mar) + safe(avr) + safe(mai) +
               safe(jui) + safe(jul) + safe(aou) + safe(sep) +
               safe(oct) + safe(nov) + safe(dec);
    }

    public String getTotalFormatted() {
        return formatMontant(getTotal());
    }

    private long safe(Long val) {
        return val == null ? 0L : val;
    }
    
    public boolean isJanPaid() { return safe(jan) > 0; }
    public boolean isFevPaid() { return safe(fev) > 0; }
    public boolean isMarPaid() { return safe(mar) > 0; }
    public boolean isAvrPaid() { return safe(avr) > 0; }
    public boolean isMaiPaid() { return safe(mai) > 0; }
    public boolean isJuiPaid() { return safe(jui) > 0; }
    public boolean isJulPaid() { return safe(jul) > 0; }
    public boolean isAouPaid() { return safe(aou) > 0; }
    public boolean isSepPaid() { return safe(sep) > 0; }
    public boolean isOctPaid() { return safe(oct) > 0; }
    public boolean isNovPaid() { return safe(nov) > 0; }
    public boolean isDecPaid() { return safe(dec) > 0; }
    
    public String getStyle(Long montant) {
        if (montant == null || montant == 0) return "not-paid";
        if (montant < 50000) return "partial"; // à adapter
        return "paid";
    }
    
    public String getLocataireCourt() {
        return truncate(locataire, 20);
    }

    private String truncate(String text, int max) {
        if (text == null) return "";
        return text.length() > max ? text.substring(0, max) + "..." : text;
    }
  
}