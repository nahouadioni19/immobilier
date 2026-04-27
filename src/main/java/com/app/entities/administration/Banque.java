package com.app.entities.administration;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="t_banque")
@Audited
@DynamicUpdate
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Banque {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "banque_seq")
	@SequenceGenerator(name = "banque_seq", sequenceName = "SEQ_BANK", allocationSize = 1)
	protected Long bancode;

	// Getters & Setters
	@Column(length = 100)
	private String banlibelle;
	@Column(length = 30)
	private String bansigle;
	
	public Long getBancode() {
		return bancode;
	}
	public void setBancode(Long bancode) {
		this.bancode = bancode;
	}
	public String getBanlibelle() {
		return banlibelle;
	}
	public void setBanlibelle(String banlibelle) {
		this.banlibelle = banlibelle;
	}
	public String getBansigle() {
		return bansigle;
	}
	public void setBansigle(String bansigle) {
		this.bansigle = bansigle;
	}
	
}
