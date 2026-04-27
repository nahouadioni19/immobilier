package com.app.dto;

import java.util.HashMap;
import java.util.Map;

public class DashboardLoyerDTO {

    private String locataire;

    private Integer jan;
    private Integer fev;
    private Integer mar;
    private Integer avr;
    private Integer mai;
    private Integer jui;
    private Integer jul;
    private Integer aou;
    private Integer sep;
    private Integer oct;
    private Integer nov;
    private Integer dec;
    
    public DashboardLoyerDTO() {
    	
    }
    
    public DashboardLoyerDTO(String locataire, Integer jan, Integer fev, Integer mar, 
		 				  Integer avr, Integer mai, Integer jui, Integer jul, Integer aou,
		 				  Integer sep, Integer oct, Integer nov, Integer dec) {
	 
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
// getters/setters
    
	public String getLocataire() {
		return locataire;
	}
	
	public void setLocataire(String locataire) {
		this.locataire = locataire;
	}

	public Integer getJan() {
		return jan;
	}

	public void setJan(Integer jan) {
		this.jan = jan;
	}

	public Integer getFev() {
		return fev;
	}

	public void setFev(Integer fev) {
		this.fev = fev;
	}

	public Integer getMar() {
		return mar;
	}

	public void setMar(Integer mar) {
		this.mar = mar;
	}

	public Integer getAvr() {
		return avr;
	}

	public void setAvr(Integer avr) {
		this.avr = avr;
	}

	public Integer getMai() {
		return mai;
	}

	public void setMai(Integer mai) {
		this.mai = mai;
	}

	public Integer getJui() {
		return jui;
	}

	public void setJui(Integer jui) {
		this.jui = jui;
	}

	public Integer getJul() {
		return jul;
	}

	public void setJul(Integer jul) {
		this.jul = jul;
	}

	public Integer getAou() {
		return aou;
	}

	public void setAou(Integer aou) {
		this.aou = aou;
	}

	public Integer getSep() {
		return sep;
	}

	public void setSep(Integer sep) {
		this.sep = sep;
	}

	public Integer getOct() {
		return oct;
	}

	public void setOct(Integer oct) {
		this.oct = oct;
	}

	public Integer getNov() {
		return nov;
	}

	public void setNov(Integer nov) {
		this.nov = nov;
	}

	public Integer getDec() {
		return dec;
	}

	public void setDec(Integer dec) {
		this.dec = dec;
	}
    
}