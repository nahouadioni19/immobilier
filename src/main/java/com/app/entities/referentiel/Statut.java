package com.app.entities.referentiel;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;

import com.app.entities.BaseEntityWithCodeLibelle;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Audited
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "t_statut")
@DynamicUpdate
//@SequenceGenerator(name = "statut_gen", sequenceName = "statut_seq", allocationSize = 1)
public class Statut extends BaseEntityWithCodeLibelle {
    
	/*
	 * @Override
	 * 
	 * @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "statut_gen")
	 * 
	 * @Id public Integer getId() { return super.getId(); }
	 */
}

