package com.app.entities.referentiel;

import com.app.entities.BaseEntityWithCodeLibelle;
import com.app.entities.administration.Ministere;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

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
@Table(name = "t_ville")
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
//@SequenceGenerator(name = "ville_gen", sequenceName = "ville_seq", allocationSize = 1)
public class Ville extends BaseEntityWithCodeLibelle{

	private static final long serialVersionUID = 1L;
	/*
	 * @Override
	 * 
	 * @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ville_gen")
	 * 
	 * @Id public Integer getId() { return super.getId(); }
	 */
	
	@ManyToOne
    @JoinColumn(name="pays_id", referencedColumnName = "idt", nullable = true)
    private Pays pays;
	
}
