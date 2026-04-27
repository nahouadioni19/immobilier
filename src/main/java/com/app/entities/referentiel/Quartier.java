package com.app.entities.referentiel;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "t_quartier")
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
//@SequenceGenerator(name = "quartier_gen", sequenceName = "quartier_seq", allocationSize = 1)
public class Quartier extends BaseEntityWithCodeLibelle{

	private static final long serialVersionUID = 1L;
	
	/*
	 * @Override
	 * 
	 * @GeneratedValue(strategy = GenerationType.SEQUENCE, generator =
	 * "quartier_gen")
	 * 
	 * @Id public Integer getId() { return super.getId(); }
	 */
	
	@ManyToOne
    @JoinColumn(name="ville_id", referencedColumnName = "idt", nullable = true)
    private Ville ville;
	
}
