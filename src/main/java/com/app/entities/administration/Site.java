package com.app.entities.administration;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "t_site")
@Audited
@DynamicUpdate
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Site {
	
	@Id
	@Column(length = 10)
	private String sitCode;
	
	@Column(name="SIT_LIBELLE",length = 70)
	private String sitLibelle;
	
	@Column(name="SIT_ADRESSE", length = 50)
	private String sitAdresse;
	
	@Column(name="SIT_TEL", length = 50)
	private String sitTel;
	
	@Column(length = 10) 
	private String sitSiteCode;
	
	@Column(length = 5)
	private String sitVilCode;
		 
	public String getSitCode() {
		return sitCode;
	}

	public void setSitCode(String sitCode) {
		this.sitCode = sitCode;
	}

	public String getSitLibelle() {
		return sitLibelle;
	}

	public void setSitLibelle(String sitLibelle) {
		this.sitLibelle = sitLibelle;
	}

	public String getSitAdresse() {
		return sitAdresse;
	}

	public void setSitAdresse(String sitAdresse) {
		this.sitAdresse = sitAdresse;
	}

	public String getSitTel() {
		return sitTel;
	}

	public void setSitTel(String sitTel) {
		this.sitTel = sitTel;
	}

	public String getSitSiteCode() {
		return sitSiteCode;
	}

	public void setSitSiteCode(String sitSiteCode) {
		this.sitSiteCode = sitSiteCode;
	}

	public String getSitVilCode() {
		return sitVilCode;
	}

	public void setSitVilCode(String sitVilCode) {
		this.sitVilCode = sitVilCode;
	}
	
}
