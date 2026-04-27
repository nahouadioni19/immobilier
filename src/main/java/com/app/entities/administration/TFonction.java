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
@Table(name = "t_fonction")
public class TFonction {
	
	/*
	 * PAY_CODE VARCHAR2(3 BYTE) TYF_COD VARCHAR2(10 BYTE) FON_DAT_DEB DATE
	 * FON_DAT_FIN DATE FON_LIBELLE VARCHAR2(100 BYTE) FON_ADR VARCHAR2(100 BYTE)
	 * FON_TEL VARCHAR2(50 BYTE) FON_MOBIL VARCHAR2(20 BYTE) FON_EMAIL VARCHAR2(50
	 * BYTE) MIN_CODE VARCHAR2(10 BYTE) VISA_IMG VARCHAR2(20 BYTE) FON_SIT_CODE
	 * VARCHAR2(10 BYTE)
	 */
	@Id
	@Column(length = 20)
	private String fonCod;
}
