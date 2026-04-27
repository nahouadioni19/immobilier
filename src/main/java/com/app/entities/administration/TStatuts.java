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

@Audited
@DynamicUpdate
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_statuts")
public class TStatuts {

	@Id
	@Column(length = 10)
	private String staCode;
	
	@Column(length = 100)
	private String staLibelle;

	public String getStaCode() {
		return staCode;
	}

	public void setStaCode(String staCode) {
		this.staCode = staCode;
	}

	public String getStaLibelle() {
		return staLibelle;
	}

	public void setStaLibelle(String staLibelle) {
		this.staLibelle = staLibelle;
	}
	
}
